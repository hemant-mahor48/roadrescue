package com.roadrescue.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Validated
public class MechanicRegistrationRequest {
    @NotNull(message = "Latitude is required")
    private BigDecimal currentLocationLat;

    @NotNull(message = "Longitude is required")
    private BigDecimal currentLocationLng;
}
