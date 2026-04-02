package com.roadrescue.analytics_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "payments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAnalytics {
    @Id
    private String id;
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
