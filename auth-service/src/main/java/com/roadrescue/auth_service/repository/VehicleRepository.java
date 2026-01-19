package com.roadrescue.auth_service.repository;

import com.roadrescue.auth_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Boolean existsByRegistrationNumber(String registrationNumber);
}
