package com.roadrescue.auth_service.service.serviceImpl;

import com.roadrescue.auth_service.dto.AddVehicleRequest;
import com.roadrescue.auth_service.dto.UpdateProfileRequest;
import com.roadrescue.auth_service.dto.UserDTO;
import com.roadrescue.auth_service.dto.VehicleDTO;
import com.roadrescue.auth_service.exceptions.DuplicateResourceException;
import com.roadrescue.auth_service.exceptions.ResourceNotFoundException;
import com.roadrescue.auth_service.model.User;
import com.roadrescue.auth_service.model.Vehicle;
import com.roadrescue.auth_service.repository.UserRepository;
import com.roadrescue.auth_service.repository.VehicleRepository;
import com.roadrescue.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public UserDTO updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updated = userRepository.save(user);
        log.info("Profile updated for user: {}", email);

        return modelMapper.map(updated, UserDTO.class);
    }

    @Transactional
    public VehicleDTO addVehicle(String email, AddVehicleRequest request) {
        // Check if registration number already exists
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle with this registration number already exists");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vehicle vehicle = new Vehicle();
        vehicle.setUser(user);
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setManufacturer(request.getManufacturer());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setColor(request.getColor());

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle added for user: {}", email);

        return modelMapper.map(saved, VehicleDTO.class);
    }
}
