package com.roadrescue.matching_service.client;

import com.roadrescue.matching_service.dto.ApiResponse;
import com.roadrescue.matching_service.dto.MechanicProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface UserFeignClient {

    @GetMapping("/api/v1/mechanics/{mechanicId}/profile")
    ApiResponse<MechanicProfileDTO> getMechanicProfile(@PathVariable UUID mechanicId);
}
