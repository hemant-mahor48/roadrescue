package com.roadrescue.auth_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
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
