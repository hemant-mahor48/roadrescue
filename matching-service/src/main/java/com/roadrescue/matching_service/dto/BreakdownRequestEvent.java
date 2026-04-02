package com.roadrescue.matching_service.dto;

import com.roadrescue.matching_service.model.IssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BreakdownRequestEvent {
    private UUID requestId;
    private UUID userId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private IssueType issueType;
    private List<UUID> excludedMechanicIds;
    private LocalDateTime timestamp;
}
