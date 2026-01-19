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
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        UserDTO updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping("/me/vehicles")
    public ResponseEntity<ApiResponse<VehicleDTO>> addVehicle(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddVehicleRequest request
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        VehicleDTO vehicle = userService.addVehicle(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Vehicle added successfully", vehicle));
    }

    @GetMapping("/me/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getMyVehicles(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        UserDTO userDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Vehicles fetched successfully", userDTO.getVehicles()));
    }
}
