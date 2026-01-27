package com.roadrescue.matching_service.dto;

import com.roadrescue.matching_service.model.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MechanicNotificationEvent {
    private UUID requestId;
    private UUID mechanicId;
    private BigDecimal customerLatitude;
    private BigDecimal customerLongitude;
    private Double estimatedDistance;
    private IssueType issueType;
    private LocalDateTime timestamp;
}