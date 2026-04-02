package com.roadrescue.rating_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestSummaryDTO {
    private UUID id;
    private UUID userId;
    private UUID mechanicId;
    private String status;
}
