package com.roadrescue.request_service.service.serviceImpl;

import com.roadrescue.request_service.client.UserFeignClient;
import com.roadrescue.request_service.dto.ApiResponse;
import com.roadrescue.request_service.dto.BreakdownRequestDTO;
import com.roadrescue.request_service.dto.UserDTO;
import com.roadrescue.request_service.mapper.RequestMapper;
import com.roadrescue.request_service.model.IssueType;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.model.RequestStatus;
import com.roadrescue.request_service.repository.RequestRepository;
import com.roadrescue.request_service.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private KafkaProducerService kafkaProducerService;

    private RequestServiceImpl requestService;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(
                requestRepository,
                userFeignClient,
                new RequestMapper(),
                kafkaProducerService
        );
    }

    @Test
    void getMyRequests_allowsRequestsWithoutAssignedMechanic() {
        UUID customerId = UUID.randomUUID();
        Request request = Request.builder()
                .id(UUID.randomUUID())
                .userId(customerId)
                .locationLatitude(BigDecimal.valueOf(12.97))
                .locationLongitude(BigDecimal.valueOf(77.59))
                .issueType(IssueType.BATTERY_ISSUE)
                .description("Battery dead")
                .status(RequestStatus.SEARCHING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserDTO currentUser = UserDTO.builder()
                .id(customerId)
                .email("customer@example.com")
                .build();

        when(userFeignClient.getCurrentUser("customer@example.com"))
                .thenReturn(Optional.of(ApiResponse.success("ok", currentUser)));
        when(requestRepository.findByUserId(customerId)).thenReturn(List.of(request));

        List<BreakdownRequestDTO> result = requestService.getMyRequests("customer@example.com");

        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
        assertNull(result.get(0).getMechanicId());
        assertNull(result.get(0).getMechanicName());
        verify(userFeignClient, never()).getCurrentMechanicById(any());
    }

    @Test
    void markArrived_movesRequestToInProgressAndStartsTimer() {
        UUID requestId = UUID.randomUUID();
        Request request = Request.builder()
                .id(requestId)
                .status(RequestStatus.EN_ROUTE)
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        requestService.markArrived(requestId);

        ArgumentCaptor<Request> savedRequest = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository).save(savedRequest.capture());
        assertEquals(RequestStatus.IN_PROGRESS, savedRequest.getValue().getStatus());
        assertNotNull(savedRequest.getValue().getServiceStartedAt());
    }
}
