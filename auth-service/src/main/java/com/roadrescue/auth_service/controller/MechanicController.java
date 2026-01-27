package com.roadrescue.auth_service.controller;

import com.roadrescue.auth_service.dto.ApiResponse;
import com.roadrescue.auth_service.dto.MechanicProfileDTO;
import com.roadrescue.auth_service.dto.MechanicRegistrationRequest;
import com.roadrescue.auth_service.dto.MechanicVerificationRequest;
import com.roadrescue.auth_service.service.MechanicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mechanics")
@Slf4j
@RequiredArgsConstructor
public class MechanicController {

    private final MechanicService mechanicService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<MechanicProfileDTO>> registerAsMechanic(
            @RequestHeader("X-auth-user") String email,
            @Valid @RequestBody MechanicRegistrationRequest request
    ) {
        MechanicProfileDTO response = mechanicService.registerMechanic(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mechanic registered successfully", response));
    }

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<MechanicProfileDTO>> submitMechanicVerification(
            @RequestHeader("X-auth-user") String email,
            @Valid @RequestBody MechanicVerificationRequest request
    ) {
        MechanicProfileDTO response = mechanicService.submitVerification(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Mechanic verification submitted successfully", response));
    }

    /**
     * Mechanic updates their current location
     * This should be called:
     * 1. When mechanic goes online/available
     * 2. Every 10-30 seconds while mechanic is moving
     */
    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @RequestHeader("X-auth-user") String email,
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng) {

        mechanicService.updateLocation(email, lat, lng);
        return ResponseEntity.ok(ApiResponse.success("Location updated", null));
    }

    /**
     * Mechanic toggles availability (online/offline)
     */
    @PutMapping("/availability")
    public ResponseEntity<ApiResponse<Void>> updateAvailability(
            @RequestHeader("X-auth-user") String email,
            @RequestParam Boolean available) {

        mechanicService.updateAvailability(email, available);
        return ResponseEntity.ok(ApiResponse.success("Availability updated", null));
    }

    @GetMapping("/{mechanicId}/profile")
    public ResponseEntity<ApiResponse<MechanicProfileDTO>> getMechanicProfile(
            @PathVariable UUID mechanicId) {
        MechanicProfileDTO profile = mechanicService.getMechanicProfile(mechanicId);
        return ResponseEntity.ok(ApiResponse.success("Mechanic profile fetched", profile));
    }
}
