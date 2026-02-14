package com.roadrescue.request_service.client;

import com.roadrescue.request_service.dto.ApiResponse;
import com.roadrescue.request_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://localhost:8999")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/me")
    Optional<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("X-auth-user") String email);

    @GetMapping("/api/v1/users/me/{mechanicId}")
    Optional<ApiResponse<UserDTO>> getCurrentMechanicById(@PathVariable UUID mechanicId);
}
