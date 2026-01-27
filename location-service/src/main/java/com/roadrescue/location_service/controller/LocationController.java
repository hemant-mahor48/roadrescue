package com.roadrescue.location_service.controller;

import com.roadrescue.location_service.dto.MechanicLocation;
import com.roadrescue.location_service.dto.NearbyMechanic;
import com.roadrescue.location_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/mechanics/{mechanicId}")
    public ResponseEntity<Void> updateLocation(
            @PathVariable UUID mechanicId,
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng) {
        locationService.updateMechanicLocation(mechanicId, lat, lng);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyMechanic>> findNearby(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        List<NearbyMechanic> mechanics = locationService
                .findNearbyMechanics(lat, lng, radiusKm);
        return ResponseEntity.ok(mechanics);
    }

    @PutMapping("/mechanics/{mechanicId}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable UUID mechanicId,
            @RequestParam Boolean available) {
        locationService.setMechanicAvailability(mechanicId, available);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mechanics/{mechanicId}")
    public ResponseEntity<MechanicLocation> getLocation(
            @PathVariable UUID mechanicId) {
        MechanicLocation location = locationService.getMechanicLocation(mechanicId);
        return ResponseEntity.ok(location);
    }
}
