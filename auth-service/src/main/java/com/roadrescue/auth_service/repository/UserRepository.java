package com.roadrescue.auth_service.repository;

import com.roadrescue.auth_service.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(@NotNull(message = "Email is required") @Email(message = "Please provide a valid email") String email);

    Optional<User> findByEmail(@NotNull(message = "Email is required") @Email(message = "Please provide a valid email") String email);
}
