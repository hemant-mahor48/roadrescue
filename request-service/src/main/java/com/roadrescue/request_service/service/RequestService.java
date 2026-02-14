package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestDTO;

import java.util.List;
import java.util.UUID;

public interface RequestService {
    String createRequest(String email, BreakdownRequest request);

    List<BreakdownRequestDTO> getMyRequests(String email);

    BreakdownRequestDTO getRequestById(UUID requestId, String email);

    void acceptRequest(UUID requestId, String mechanicEmail);

    void rejectRequest(UUID requestId, String mechanicEmail);
}