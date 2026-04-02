package com.roadrescue.analytics_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "service_completions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCompletionAnalytics {
    @Id
    private String id;
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
