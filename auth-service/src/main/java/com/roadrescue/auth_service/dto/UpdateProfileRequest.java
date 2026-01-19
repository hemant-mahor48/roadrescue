package com.roadrescue.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UpdateProfileRequest {
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255)
    private String fullName;

    @Pattern(regexp = "^https?://.*", message = "Invalid URL format")
    private String profileImageUrl;
}