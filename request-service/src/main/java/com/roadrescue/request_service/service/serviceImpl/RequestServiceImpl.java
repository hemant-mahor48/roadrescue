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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserFeignClient userFeignClient;
    private final RequestMapper requestMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public String createRequest(String email, BreakdownRequest breakdownRequest) {
        UserDTO userDTO = userFeignClient.getCurrentUser(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found!!")).getData();

        Request request = requestMapper.toRequest(breakdownRequest, userDTO);
        Request savedRequest = requestRepository.save(request);

        BreakdownRequestEvent event = requestMapper.toBreakdownRequestEvent(savedRequest, userDTO);
        kafkaProducerService.sendEvent(event);
        return savedRequest.getId().toString();
    }
}
