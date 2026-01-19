package com.roadrescue.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotNull(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotNull(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Role is required")
    private String role;
}
