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
public class BreakdownRequestEvent {
    private UUID requestId;
    private UUID userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String issueType;
    private LocalDateTime timestamp;
}