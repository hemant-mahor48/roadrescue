package com.roadrescue.rating_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    private UUID mechanicId;
    private UUID requestId;
    private UUID customerId;
    private Integer rating;
    private String review;
    private Double newAvgRating;
    private Integer totalReviews;
    private LocalDateTime createdAt;
}
