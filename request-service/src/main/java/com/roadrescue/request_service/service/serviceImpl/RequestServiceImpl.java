package com.roadrescue.request_service.service.serviceImpl;

import com.roadrescue.request_service.client.UserFeignClient;
import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestEvent;
import com.roadrescue.request_service.dto.UserDTO;
import com.roadrescue.request_service.exceptions.UserNotFoundException;
import com.roadrescue.request_service.mapper.RequestMapper;
import com.roadrescue.request_service.model.Request;
import com.roadrescue.request_service.repository.RequestRepository;
import com.roadrescue.request_service.service.KafkaProducerService;
import com.roadrescue.request_service.service.RequestService;
import feign.FeignException;
import jakarta.ws.rs.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
