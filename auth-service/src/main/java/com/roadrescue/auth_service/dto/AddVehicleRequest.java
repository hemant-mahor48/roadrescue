package com.roadrescue.auth_service.dto;

import com.roadrescue.auth_service.model.VehicleType;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class AddVehicleRequest {
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Model is required")
    private String model;

    @Min(value = 1900, message = "Invalid year")
    @Max(value = 2030, message = "Invalid year")
    private Integer year;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}-\\d{2}-[A-Z]{1,2}-\\d{4}$",
            message = "Invalid registration format (e.g., DL-01-AB-1234)")
    private String registrationNumber;

    private String color;
}
