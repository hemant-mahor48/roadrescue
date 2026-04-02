package com.roadrescue.request_service.service.serviceImpl;

import com.roadrescue.request_service.client.UserFeignClient;
import com.roadrescue.request_service.dto.*;
import com.roadrescue.request_service.exceptions.BusinessException;
import com.roadrescue.request_service.exceptions.UserNotFoundException;
import com.roadrescue.request_service.mapper.RequestMapper;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;
import com.roadrescue.request_service.repository.RequestRepository;
import com.roadrescue.request_service.service.KafkaProducerService;
import com.roadrescue.request_service.service.RequestService;
import feign.FeignException;
import jakarta.ws.rs.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserFeignClient userFeignClient;
    private final RequestMapper requestMapper;
    private final KafkaProducerService kafkaProducerService;

    private static final Set<RequestStatus> TERMINAL_OR_LATER = EnumSet.of(
            RequestStatus.EN_ROUTE,
            RequestStatus.IN_PROGRESS,
            RequestStatus.COMPLETED,
            RequestStatus.CANCELLED,
            RequestStatus.PAYMENT_PENDING
    );

    @Override
    public String createRequest(String email, BreakdownRequest breakdownRequest) {
        try {
            UserDTO userDTO = userFeignClient.getCurrentUser(email)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!!")).getData();

            validateVehicleSelection(userDTO, breakdownRequest.getVehicleId());
            Request request = requestMapper.toRequest(breakdownRequest, userDTO);
            Request savedRequest = requestRepository.save(request);

            BreakdownRequestEvent event = requestMapper.toBreakdownRequestEvent(savedRequest, userDTO);
            try {
                kafkaProducerService.sendEvent(event);
                savedRequest.setStatus(RequestStatus.SEARCHING);
                requestRepository.save(savedRequest);
            } catch (Exception e) {
                log.error("Failed to publish event for request: {}", savedRequest.getId(), e);
            }
            return savedRequest.getId().toString();
        } catch (FeignException e) {
            log.error("Failed to fetch user details", e);
            throw new ServiceUnavailableException("User service is currently unavailable");
        }
    }

    @Override
    public List<BreakdownRequestDTO> getMyRequests(String email) {
        List<BreakdownRequestDTO> myRequests = new ArrayList<>();
        try {
            UserDTO userDTO = userFeignClient.getCurrentUser(email)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!!"))
                    .getData();

            List<Request> requests = requestRepository.findByUserId(userDTO.getId());
            for(Request req : requests) {
                myRequests.add(requestMapper.convertToBreakdownRequestDTO(
                        req,
                        resolveMechanic(req.getMechanicId())
                ));
            }
            return myRequests;
        } catch (FeignException e) {
            log.error("Failed to fetch user details", e);
            throw new ServiceUnavailableException("User service is currently unavailable");
        }
    }

    @Override
    public BreakdownRequestDTO getRequestById(UUID requestId, String email) {
        try {
            UserDTO userDTO = userFeignClient.getCurrentUser(email)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!!"))
                    .getData();

            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

            // Verify the request belongs to the user
            if (!request.getUserId().equals(userDTO.getId())) {
                throw new BusinessException("You don't have permission to view this request");
            }

            return requestMapper.convertToBreakdownRequestDTO(
                    request,
                    resolveMechanic(request.getMechanicId())
            );
        } catch (FeignException e) {
            log.error("Failed to fetch user details", e);
            throw new ServiceUnavailableException("User service is currently unavailable");
        }
    }

    @Transactional
    @Override
    public void acceptRequest(UUID requestId, String mechanicEmail, AcceptRequestPayload payload) {
        // Get mechanic profile
        UserDTO mechanicUser = userFeignClient.getCurrentUser(mechanicEmail)
                .orElseThrow(() -> new UserNotFoundException("Mechanic not found"))
                .getData();

        if (mechanicUser.getMechanicProfile() == null) {
            throw new BusinessException("User is not a mechanic");
        }

        // Update request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING &&
                request.getStatus() != RequestStatus.SEARCHING) {
            throw new BusinessException("Request already assigned or completed");
        }

        request.setMechanicId(mechanicUser.getMechanicProfile().getId());
        request.setStatus(RequestStatus.ASSIGNED);
        requestRepository.save(request);

        double estimatedAmount = sanitizeEstimatedAmount(payload != null ? payload.getEstimatedPayment() : null);
        double depositHoldAmount = 200.0;

        // Publish mechanic-assignment event with customerId
        MechanicAssignmentEvent event = MechanicAssignmentEvent.builder()
                .requestId(requestId)
                .mechanicId(mechanicUser.getMechanicProfile().getId())
                .customerId(request.getUserId())
                .estimatedAmount(estimatedAmount)
                .depositHoldAmount(depositHoldAmount)
                .status("ASSIGNED")
                .assignedAt(LocalDateTime.now())
                .build();

        kafkaProducerService.sendMechanicAssignmentEvent(event);

        log.info("Request {} assigned to mechanic {}, customer {} notified",
                requestId, mechanicUser.getMechanicProfile().getId(), request.getUserId());
    }

    @Override
    @Transactional
    public void rejectRequest(UUID requestId, String mechanicEmail) {
        UserDTO userDTO = userFeignClient.getCurrentUser(mechanicEmail)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!!")).getData();

        // Update request status back to SEARCHING
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        UUID rejectedMechanicId = userDTO.getMechanicProfile() != null
                ? userDTO.getMechanicProfile().getId()
                : null;

        List<UUID> excludedMechanicIds = new ArrayList<>(
                Optional.ofNullable(request.getExcludedMechanicIds()).orElse(List.of())
        );
        if (rejectedMechanicId != null && !excludedMechanicIds.contains(rejectedMechanicId)) {
            excludedMechanicIds.add(rejectedMechanicId);
        }

        request.setMechanicId(null);
        request.setStatus(RequestStatus.SEARCHING);
        request.setExcludedMechanicIds(excludedMechanicIds);
        requestRepository.save(request);

        // Publish rejection event (Matching service will find next mechanic)
        BreakdownRequestEvent event = requestMapper.toBreakdownRequestEvent(request, userDTO);

        kafkaProducerService.sendMechanicRejectionEvent(event);

        log.info("Request {} rejected, searching for next mechanic", requestId);
    }

    @Override
    @Transactional
    public void markEnRoute(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (TERMINAL_OR_LATER.contains(request.getStatus())) {
            log.debug("markEnRoute no-op: request {} already in status {}",
                    requestId, request.getStatus());
            return;
        }

        if (request.getStatus() != RequestStatus.ASSIGNED) {
            log.warn("markEnRoute unexpected status {} for request {} — skipping",
                    request.getStatus(), requestId);
            return;
        }

        request.setStatus(RequestStatus.EN_ROUTE);
        requestRepository.save(request);
        log.info("Request {} transitioned ASSIGNED → EN_ROUTE", requestId);
    }
    @Override
    @Transactional
    public void markArrived(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found: " + requestId));

        if (EnumSet.of(
                RequestStatus.IN_PROGRESS,
                RequestStatus.COMPLETED,
                RequestStatus.CANCELLED,
                RequestStatus.PAYMENT_PENDING
        ).contains(request.getStatus())) {
            log.debug("markArrived no-op: request {} already in status {}",
                    requestId, request.getStatus());
            return;
        }

        if (request.getStatus() != RequestStatus.EN_ROUTE && request.getStatus() != RequestStatus.ASSIGNED) {
            log.warn("markArrived unexpected status {} for request {} - skipping",
                    request.getStatus(), requestId);
            return;
        }

        RequestStatus previousStatus = request.getStatus();
        request.setStatus(RequestStatus.IN_PROGRESS);
        if (request.getServiceStartedAt() == null) {
            request.setServiceStartedAt(LocalDateTime.now());
        }

        requestRepository.save(request);
        log.info("Request {} transitioned {} -> IN_PROGRESS and service timer started at {}",
                requestId, previousStatus, request.getServiceStartedAt());
    }

    @Override
    @Transactional
    public void completeRequest(UUID requestId, String mechanicEmail, ServiceCompletionRequest completionRequest) {
        UserDTO mechanicUser = userFeignClient.getCurrentUser(mechanicEmail)
                .orElseThrow(() -> new UserNotFoundException("Mechanic not found"))
                .getData();

        if (mechanicUser.getMechanicProfile() == null) {
            throw new BusinessException("User is not a mechanic");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!Objects.equals(request.getMechanicId(), mechanicUser.getMechanicProfile().getId())) {
            throw new BusinessException("Only the assigned mechanic can complete this request");
        }

        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new BusinessException("Request must be in progress before completion");
        }

        double totalAmount = completionRequest.getLaborCharge() + completionRequest.getPartsCharge();
        LocalDateTime completedAt = LocalDateTime.now();
        int serviceDurationMins = request.getServiceStartedAt() == null
                ? 0
                : Math.max(1, (int) ChronoUnit.MINUTES.between(request.getServiceStartedAt(), completedAt));

        request.setServiceNotes(completionRequest.getServiceNotes().trim());
        request.setBeforeServicePhotos(sanitizeStringList(completionRequest.getBeforeServicePhotos()));
        request.setAfterServicePhotos(sanitizeStringList(completionRequest.getAfterServicePhotos()));
        request.setPartsUsed(sanitizeStringList(completionRequest.getPartsUsed()));
        request.setLaborCharge(completionRequest.getLaborCharge());
        request.setPartsCharge(completionRequest.getPartsCharge());
        request.setFinalAmount(totalAmount);
        request.setCompletedAt(completedAt);
        request.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(request);

        ServiceCompletionEvent event = ServiceCompletionEvent.builder()
                .requestId(request.getId())
                .customerId(request.getUserId())
                .mechanicId(request.getMechanicId())
                .serviceDurationMins(serviceDurationMins)
                .partsUsed(request.getPartsUsed())
                .laborCharge(request.getLaborCharge())
                .partsCharge(request.getPartsCharge())
                .totalAmount(request.getFinalAmount())
                .completedAt(completedAt)
                .build();

        kafkaProducerService.sendServiceCompletionEvent(event);
        log.info("Request {} completed by mechanic {} with total amount {}",
                requestId, request.getMechanicId(), totalAmount);
    }

    @Override
    @Transactional
    public void handlePaymentUpdate(PaymentEvent paymentEvent) {
        Request request = requestRepository.findById(paymentEvent.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setStatus("SUCCESS".equalsIgnoreCase(paymentEvent.getStatus())
                ? RequestStatus.PAID
                : RequestStatus.PAYMENT_PENDING);
        requestRepository.save(request);

        log.info("Request {} payment status updated to {}",
                paymentEvent.getRequestId(), request.getStatus());
    }

    private UserDTO resolveMechanic(UUID mechanicId) {
        if (mechanicId == null) {
            return null;
        }

        try {
            return userFeignClient.getCurrentMechanicById(mechanicId)
                    .orElseThrow(() -> new UserNotFoundException("Mechanic Not Found!!"))
                    .getData();
        } catch (Exception e) {
            log.warn("Could not load mechanic {} details for request projection: {}",
                    mechanicId, e.getMessage());
            return null;
        }
    }

    private void validateVehicleSelection(UserDTO userDTO, UUID selectedVehicleId) {
        List<VehicleDTO> vehicles = Optional.ofNullable(userDTO.getVehicles()).orElse(List.of());
        boolean vehicleExists = vehicles.stream()
                .map(VehicleDTO::getId)
                .filter(Objects::nonNull)
                .anyMatch(selectedVehicleId::equals);

        if (!vehicleExists) {
            throw new BusinessException("Selected vehicle does not belong to the authenticated user");
        }
    }

    private List<String> sanitizeStringList(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    private double sanitizeEstimatedAmount(Double estimatedPayment) {
        if (estimatedPayment == null || estimatedPayment <= 0) {
            return 0.0;
        }
        return Math.round(estimatedPayment * 100.0) / 100.0;
    }
}
