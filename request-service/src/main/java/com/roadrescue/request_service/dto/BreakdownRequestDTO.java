package com.roadrescue.request_service.dto;

import com.roadrescue.request_service.model.IssueType;
import com.roadrescue.request_service.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BreakdownRequestDTO {
    private UUID id;
    private UUID userId;
    private List<UUID> vehicleIds;
    private BigDecimal locationLatitude;
    private BigDecimal locationLongitude;
    private String address;
    private IssueType issueType;
    private String description;
    private UUID mechanicId;
    private String mechanicName;
    private String mechanicPhone;
    private RequestStatus status;
    private List<String> partsUsed;
    private Double laborCharge;
    private Double partsCharge;
    private Double finalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
