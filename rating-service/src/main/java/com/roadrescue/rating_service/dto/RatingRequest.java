package com.roadrescue.rating_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RatingRequest {
    @NotNull
    private UUID requestId;
    @NotNull
    private UUID mechanicId;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;
    private String review;
}
