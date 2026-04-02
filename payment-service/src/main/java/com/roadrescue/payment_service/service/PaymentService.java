package com.roadrescue.payment_service.service;

import com.roadrescue.payment_service.dto.MechanicAssignmentEvent;
import com.roadrescue.payment_service.dto.PaymentSummaryDTO;
import com.roadrescue.payment_service.dto.ProcessPaymentRequest;
import com.roadrescue.payment_service.dto.ServiceCompletionEvent;

import java.util.UUID;

public interface PaymentService {
    void createEstimatedPaymentHold(MechanicAssignmentEvent event);
    void createPendingPayment(ServiceCompletionEvent event);
    PaymentSummaryDTO getPaymentByRequestId(UUID requestId);
    PaymentSummaryDTO processPayment(UUID requestId, ProcessPaymentRequest request);
}
