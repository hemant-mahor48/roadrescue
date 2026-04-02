package com.roadrescue.analytics_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MechanicAssignmentEvent {
    private UUID requestId;
    private UUID mechanicId;
    private UUID customerId;
    private Double estimatedAmount;
    private Double depositHoldAmount;
    private String status;
    private LocalDateTime assignedAt;
}
