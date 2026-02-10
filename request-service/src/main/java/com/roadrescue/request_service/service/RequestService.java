package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequest;

import java.util.UUID;

public interface RequestService {
    String createRequest(String email, BreakdownRequest request);
    void acceptRequest(UUID requestId, String mechanicEmail);
    void rejectRequest(UUID requestId, String mechanicEmail);
}
