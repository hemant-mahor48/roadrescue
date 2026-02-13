package com.roadrescue.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MechanicNotificationEvent {
    private UUID requestId;
    private UUID mechanicId;
    private BigDecimal customerLatitude;
    private BigDecimal customerLongitude;
    private Double estimatedDistance;
    private IssueType issueType;
    private LocalDateTime timestamp;
}
