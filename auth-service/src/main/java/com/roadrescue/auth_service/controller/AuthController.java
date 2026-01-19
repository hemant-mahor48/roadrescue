package com.roadrescue.auth_service.controller;

import com.roadrescue.auth_service.dto.ApiResponse;
import com.roadrescue.auth_service.dto.AuthResponse;
import com.roadrescue.auth_service.dto.LoginRequest;
import com.roadrescue.auth_service.dto.RegisterRequest;
import com.roadrescue.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody @Valid RegisterRequest request) {
        UUID userId = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully!!", userId.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@RequestBody @Valid LoginRequest request) throws InvalidCredentialsException {
        AuthResponse authResponse = authService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful!!", authResponse));
    }

    @GetMapping("/validate")
    public Boolean isTokenValid(@RequestParam String token) {
        return authService.isTokenValid(token);
    }
}
