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

@Document(collection = "mechanic_badges")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MechanicBadgeAnalytics {
    @Id
    private String id;
    private UUID mechanicId;
    private Double averageRating;
    private Integer totalReviews;
    private Double completionRate;
    private Double averageResponseTimeMins;
    private List<String> badges;
    private LocalDateTime updatedAt;
}
