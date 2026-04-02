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
    private Boolean isAvailable;
    private BigDecimal currentLocationLat;
    private BigDecimal currentLocationLng;
    private String licenseNumber;
    private Boolean aadhaarVerified;
    private Boolean policeVerificationDone;

    private Double rating = 4.5;
    private Integer totalReviews = 0;
    private Integer totalJobs = 0;
    private Double acceptanceRate = 85.0;
    private Double averageResponseTimeMins = 5.0;
    private String specialization = "GENERAL";
}
