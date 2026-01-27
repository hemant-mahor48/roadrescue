package com.roadrescue.matching_service.dto;

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
    private Boolean aadharVerified;
    private Boolean policeVerificationDone;

    private Double rating;
    private Integer totalJobs;
    private Double acceptanceRate;
    private String specialization;
}
