package com.roadrescue.analytics_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "mechanic_assignments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MechanicAssignmentAnalytics {
    @Id
    private String id;
    private UUID requestId;
    private UUID mechanicId;
    private UUID customerId;
    private Double estimatedAmount;
    private Double depositHoldAmount;
    private Double responseTimeMins;
    private LocalDateTime assignedAt;
}
