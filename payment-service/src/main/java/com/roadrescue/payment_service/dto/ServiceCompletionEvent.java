package com.roadrescue.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCompletionEvent {
    private UUID requestId;
    private UUID customerId;
    private UUID mechanicId;
    private Integer serviceDurationMins;
    private List<String> partsUsed;
    private Double laborCharge;
    private Double partsCharge;
    private Double totalAmount;
    private LocalDateTime completedAt;
}
