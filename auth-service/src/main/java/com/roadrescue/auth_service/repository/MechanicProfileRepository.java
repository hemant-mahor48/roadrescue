package com.roadrescue.auth_service.repository;

import com.roadrescue.auth_service.model.MechanicProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MechanicProfileRepository extends JpaRepository<MechanicProfile, UUID> {
    Boolean existsByUserId(UUID userId);

    Optional<MechanicProfile> findByUserId(UUID id);
}
