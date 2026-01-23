package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequest;

public interface RequestService {
    String createRequest(String email, BreakdownRequest request);
}
