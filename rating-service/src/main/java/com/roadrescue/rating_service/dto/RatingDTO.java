package com.roadrescue.rating_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RatingDTO {
    private UUID id;
    private UUID requestId;
    private UUID customerId;
    private UUID mechanicId;
    private Integer score;
    private String review;
    private LocalDateTime createdAt;
}
