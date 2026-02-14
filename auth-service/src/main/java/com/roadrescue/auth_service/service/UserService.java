package com.roadrescue.auth_service.service;

import com.roadrescue.auth_service.dto.AddVehicleRequest;
import com.roadrescue.auth_service.dto.UpdateProfileRequest;
import com.roadrescue.auth_service.dto.UserDTO;
import com.roadrescue.auth_service.dto.VehicleDTO;

import java.util.UUID;

public interface UserService {
    UserDTO updateProfile(String email, UpdateProfileRequest request);
    VehicleDTO addVehicle(String email, AddVehicleRequest request);

    UserDTO getUserByEmail(String email);

    UserDTO getUserByMechanicId(UUID mechanicId);
}
