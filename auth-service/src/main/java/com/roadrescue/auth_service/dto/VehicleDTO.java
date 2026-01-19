package com.roadrescue.auth_service.dto;

import com.roadrescue.auth_service.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {
    private UUID id;
    private UUID userId;
    private VehicleType vehicleType;
    private String manufacturer;
    private String model;
    private Integer year;
    private String registrationNumber;
    private String color;
}
