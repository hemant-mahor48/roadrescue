package com.roadrescue.payment_service.controller;

import com.roadrescue.payment_service.dto.ApiResponse;
import com.roadrescue.payment_service.dto.PaymentSummaryDTO;
import com.roadrescue.payment_service.dto.ProcessPaymentRequest;
import com.roadrescue.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<ApiResponse<PaymentSummaryDTO>> getPayment(@PathVariable UUID requestId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment summary fetched successfully",
                paymentService.getPaymentByRequestId(requestId)
        ));
    }

    @PostMapping("/requests/{requestId}/pay")
    public ResponseEntity<ApiResponse<PaymentSummaryDTO>> processPayment(
            @PathVariable UUID requestId,
            @RequestBody @Valid ProcessPaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payment processed successfully",
                paymentService.processPayment(requestId, request)
        ));
    }
}
