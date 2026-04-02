package com.roadrescue.analytics_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "breakdown_requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BreakdownRequestAnalytics {
    @Id
    private String id;
    private UUID requestId;
    private UUID customerId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String issueType;
    private LocalDateTime requestedAt;
}
