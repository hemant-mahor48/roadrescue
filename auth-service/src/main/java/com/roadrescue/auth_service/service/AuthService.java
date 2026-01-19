package com.roadrescue.auth_service.service;

import com.roadrescue.auth_service.dto.AuthResponse;
import com.roadrescue.auth_service.dto.LoginRequest;
import com.roadrescue.auth_service.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface AuthService {
    UUID registerUser(@RequestBody @Valid RegisterRequest request);
    AuthResponse loginUser(@RequestBody @Valid LoginRequest request) throws InvalidCredentialsException;

    boolean isTokenValid(String token);
}
