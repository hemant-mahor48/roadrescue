package com.roadrescue.matching_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MechanicLocation {
    private UUID mechanicId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean isAvailable;
    private Long lastUpdated;
}
