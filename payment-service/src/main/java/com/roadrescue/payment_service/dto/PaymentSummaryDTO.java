package com.roadrescue.payment_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentSummaryDTO {
    private UUID paymentId;
    private UUID requestId;
    private Double estimatedAmount;
    private Double depositHoldAmount;
    private Boolean depositHeld;
    private LocalDateTime depositHeldAt;
    private LocalDateTime depositReleasedAt;
    private Double laborCharge;
    private Double partsCharge;
    private Double totalAmount;
    private Double platformFee;
    private Double mechanicEarning;
    private String paymentGateway;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String currency;
    private String status;
    private LocalDateTime paidAt;
}
