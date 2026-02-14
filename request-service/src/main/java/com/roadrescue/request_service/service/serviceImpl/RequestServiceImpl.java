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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserFeignClient userFeignClient;
    private final RequestMapper requestMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public String createRequest(String email, BreakdownRequest breakdownRequest) {
        try {
            UserDTO userDTO = userFeignClient.getCurrentUser(email)
                    .orElseThrow(() -> new UserNotFoundException("User Not Found!!")).getData();

            Request request = requestMapper.toRequest(breakdownRequest, userDTO);
            Request savedRequest = requestRepository.save(request);

            BreakdownRequestEvent event = requestMapper.toBreakdownRequestEvent(savedRequest, userDTO);
            try {
                kafkaProducerService.sendEvent(event);
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
                UserDTO mechanicDTO = userFeignClient.getCurrentMechanicById(req.getMechanicId())
                        .orElseThrow(() -> new UserNotFoundException("Mechanic Not Found!!"))
                        .getData();
                myRequests.add(requestMapper.convertToBreakdownRequestDTO(req, mechanicDTO));
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

            UserDTO mechanicDTO = userFeignClient.getCurrentMechanicById(request.getMechanicId())
                    .orElseThrow(() -> new UserNotFoundException("Mechanic Not Found!!"))
                    .getData();

            // Verify the request belongs to the user
            if (!request.getUserId().equals(userDTO.getId())) {
                throw new BusinessException("You don't have permission to view this request");
            }

            return requestMapper.convertToBreakdownRequestDTO(request, mechanicDTO);
        } catch (FeignException e) {
            log.error("Failed to fetch user details", e);
            throw new ServiceUnavailableException("User service is currently unavailable");
        }
    }

    @Transactional
    @Override
    public void acceptRequest(UUID requestId, String mechanicEmail) {
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

        // Publish mechanic-assignment event with customerId
        MechanicAssignmentEvent event = MechanicAssignmentEvent.builder()
                .requestId(requestId)
                .mechanicId(mechanicUser.getMechanicProfile().getId())
                .customerId(request.getUserId())
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

        request.setStatus(RequestStatus.SEARCHING);
        requestRepository.save(request);

        // Publish rejection event (Matching service will find next mechanic)
        BreakdownRequestEvent event = requestMapper.toBreakdownRequestEvent(request, userDTO);

        kafkaProducerService.sendMechanicRejectionEvent(event);

        log.info("Request {} rejected, searching for next mechanic", requestId);
    }
}