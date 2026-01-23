package com.roadrescue.request_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MechanicProfileDTO {
    private UUID id;
    private UUID userId;
    private Boolean isAvailable;
    private BigDecimal currentLocationLat;
    private BigDecimal currentLocationLng;
    private String licenseNumber;
    private String vehicleNumber;
    private Boolean aadharVerified;
    private Boolean policeVerificationDone;
}
