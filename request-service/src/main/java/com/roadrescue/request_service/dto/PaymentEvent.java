package com.roadrescue.request_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private UUID requestId;
    private UUID paymentId;
    private UUID customerId;
    private UUID mechanicId;
    private Double amount;
    private Double mechanicEarning;
    private Double platformFee;
    private String status;
    private LocalDateTime paidAt;
}
