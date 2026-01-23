package com.roadrescue.request_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@Validated
public class BreakdownRequest {

    @NotBlank(message = "Current Location Latitude is required")
    private BigDecimal currentLocationLat;

    @NotBlank(message = "Current Location Longitude is required")
    private BigDecimal currentLocationLng;

    @NotBlank(message = "Issue Type is required")
    private String issueType;

    private String description;

    private String address;
}
