package com.roadrescue.rating_service.client;

import com.roadrescue.rating_service.dto.ApiResponse;
import com.roadrescue.rating_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "AUTH-SERVICE")
public interface UserFeignClient {
    @GetMapping("/api/v1/users/me")
    Optional<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("X-auth-user") String email);
}
