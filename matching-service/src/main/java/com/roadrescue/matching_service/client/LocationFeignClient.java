package com.roadrescue.matching_service.client;

import com.roadrescue.matching_service.dto.NearbyMechanic;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "location-service")
public interface LocationFeignClient {

    @GetMapping("/api/v1/location/nearby")
    List<NearbyMechanic> findNearbyMechanics(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam Double radiusKm
    );
}
