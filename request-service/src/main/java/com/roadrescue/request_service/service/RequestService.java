package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestDTO;
import com.roadrescue.request_service.dto.AcceptRequestPayload;
import com.roadrescue.request_service.dto.PaymentEvent;
import com.roadrescue.request_service.dto.ServiceCompletionRequest;

import java.util.List;
import java.util.UUID;

public interface RequestService {
    String createRequest(String email, BreakdownRequest request);

    List<BreakdownRequestDTO> getMyRequests(String email);

    BreakdownRequestDTO getRequestById(UUID requestId, String email);

    void acceptRequest(UUID requestId, String mechanicEmail, AcceptRequestPayload payload);

    void rejectRequest(UUID requestId, String mechanicEmail);

    void markEnRoute(UUID requestId);

    void markArrived(UUID requestId);

    void completeRequest(UUID requestId, String mechanicEmail, ServiceCompletionRequest request);

    void handlePaymentUpdate(PaymentEvent paymentEvent);
}
