package com.roadrescue.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessPaymentRequest {
    @NotBlank(message = "Payment gateway is required")
    private String paymentGateway;
}
