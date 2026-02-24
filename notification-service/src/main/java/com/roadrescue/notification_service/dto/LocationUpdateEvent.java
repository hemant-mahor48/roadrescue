package com.roadrescue.notification_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdateEvent {
    private UUID requestId;
    private UUID mechanicId;
    private UUID customerId;
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private Integer etaMinutes;
    private Double distanceRemainingKm;
    private LocalDateTime timestamp;
}
