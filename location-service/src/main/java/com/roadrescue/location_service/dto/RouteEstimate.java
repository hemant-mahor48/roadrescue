package com.roadrescue.location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteEstimate {
    private Double distanceKm;
    private Integer etaMinutes;
    private String provider;
}
