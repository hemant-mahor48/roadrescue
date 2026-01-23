package com.roadrescue.request_service.dto;

import com.roadrescue.request_service.model.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BreakdownRequestEvent {
    private UUID requestId;
    private UUID userId;
    private Map<BigDecimal, BigDecimal> location;
    private IssueType issueType;
    private LocalDateTime timestamp;
}
