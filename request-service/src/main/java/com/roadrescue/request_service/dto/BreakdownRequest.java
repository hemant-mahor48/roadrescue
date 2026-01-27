package com.roadrescue.request_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Validated
public class BreakdownRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal currentLocationLat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal currentLocationLng;

    @NotBlank(message = "Issue type is required")
    @Pattern(regexp = "TIRE_PUNCTURE|BATTERY_ISSUE|ENGINE_FAILURE|FUEL_EMPTY|LOCKOUT|ACCIDENT|OTHER")
    private String issueType;

    private String description;

    private String address;
}
