package com.roadrescue.auth_service.controller;

import com.roadrescue.auth_service.dto.ApiResponse;
import com.roadrescue.auth_service.dto.MechanicProfileDTO;
import com.roadrescue.auth_service.dto.MechanicRegistrationRequest;
import com.roadrescue.auth_service.dto.MechanicVerificationRequest;
import com.roadrescue.auth_service.service.MechanicService;
import com.roadrescue.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
