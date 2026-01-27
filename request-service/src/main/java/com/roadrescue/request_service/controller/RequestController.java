package com.roadrescue.request_service.controller;

import com.roadrescue.request_service.dto.ApiResponse;
import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBreakdownRequest(@RequestHeader("X-auth-user") String email,
                                                                      @RequestBody @Valid BreakdownRequest request) {
        String requestId = requestService.createRequest(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .success("Breakdown request created successfully", requestId));
    }
}
