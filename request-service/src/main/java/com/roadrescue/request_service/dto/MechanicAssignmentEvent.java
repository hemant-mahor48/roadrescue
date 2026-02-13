package com.roadrescue.request_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MechanicAssignmentEvent {
    private UUID requestId;
    private UUID mechanicId;
    private UUID customerId;
    private String status;
    private LocalDateTime assignedAt;
}
