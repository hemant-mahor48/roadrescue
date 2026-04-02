package com.roadrescue.rating_service.client;

import com.roadrescue.rating_service.dto.ApiResponse;
import com.roadrescue.rating_service.dto.RequestSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "REQUEST-SERVICE")
public interface RequestFeignClient {

    @GetMapping("/api/v1/requests/{requestId}")
    Optional<ApiResponse<RequestSummaryDTO>> getRequestById(
            @PathVariable UUID requestId,
            @RequestHeader("X-auth-user") String email
    );
}
