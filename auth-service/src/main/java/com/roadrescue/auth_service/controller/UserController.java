package com.roadrescue.auth_service.controller;

import com.roadrescue.auth_service.dto.*;
import com.roadrescue.auth_service.service.UserService;
import com.roadrescue.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("X-auth-user") String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestHeader("X-auth-user") String email,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserDTO updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping("/me/vehicles")
    public ResponseEntity<ApiResponse<VehicleDTO>> addVehicle(
            @RequestHeader("X-auth-user") String email,
            @Valid @RequestBody AddVehicleRequest request
    ) {
        VehicleDTO vehicle = userService.addVehicle(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Vehicle added successfully", vehicle));
    }

    @GetMapping("/me/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getMyVehicles(@RequestHeader("X-auth-user") String email) {
        UserDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched successfully", userDTO.getVehicles()));
    }
}
