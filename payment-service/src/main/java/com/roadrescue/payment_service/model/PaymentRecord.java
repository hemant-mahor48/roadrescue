package com.roadrescue.payment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID requestId;
    private UUID customerId;
    private UUID mechanicId;

    private Integer serviceDurationMins;
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
    private String gatewayReference;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
