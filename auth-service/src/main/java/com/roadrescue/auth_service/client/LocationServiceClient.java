package com.roadrescue.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "location-service")
public interface LocationServiceClient {

    @PostMapping("/api/v1/location/mechanics/{mechanicId}")
    void updateLocation(
            @PathVariable UUID mechanicId,
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng
    );

    @PutMapping("/api/v1/location/mechanics/{mechanicId}/availability")
    void updateAvailability(
            @PathVariable UUID mechanicId,
            @RequestParam Boolean available
    );
}
