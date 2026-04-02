package com.roadrescue.request_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ServiceCompletionRequest {

    @NotBlank(message = "Service notes are required")
    private String serviceNotes;

    @NotEmpty(message = "At least one part used is required")
    private List<String> partsUsed;

    @Size(max = 5, message = "You can attach up to 5 before photos")
    private List<String> beforeServicePhotos;

    @Size(max = 5, message = "You can attach up to 5 after photos")
    private List<String> afterServicePhotos;

    @NotNull(message = "Labor charge is required")
    @DecimalMin(value = "0.0", message = "Labor charge must be non-negative")
    private Double laborCharge;

    @NotNull(message = "Parts charge is required")
    @DecimalMin(value = "0.0", message = "Parts charge must be non-negative")
    private Double partsCharge;
}
