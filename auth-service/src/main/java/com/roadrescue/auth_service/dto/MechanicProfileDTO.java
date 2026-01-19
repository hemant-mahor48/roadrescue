package com.roadrescue.auth_service.dto;

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
    private boolean isAvailable;
    private BigDecimal currentLocationLat;
    private BigDecimal currentLocationLng;
    private String licenseNumber;
    private String vehicleNumber;
    private boolean aadharVerified;
    private boolean policeVerificationDone;
}
