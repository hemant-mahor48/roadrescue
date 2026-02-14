package com.roadrescue.request_service.controller;

import com.roadrescue.request_service.dto.ApiResponse;
import com.roadrescue.request_service.dto.BreakdownRequest;
import com.roadrescue.request_service.dto.BreakdownRequestDTO;
import com.roadrescue.request_service.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<BreakdownRequestDTO>>> getMyRequests(
            @RequestHeader("X-auth-user") String email) {
        List<BreakdownRequestDTO> requests = requestService.getMyRequests(email);
        return ResponseEntity.ok(ApiResponse.success("Requests retrieved successfully", requests));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<BreakdownRequestDTO>> getRequestById(
            @PathVariable UUID requestId,
            @RequestHeader("X-auth-user") String email) {
        BreakdownRequestDTO request = requestService.getRequestById(requestId, email);
        return ResponseEntity.ok(ApiResponse.success("Request retrieved successfully", request));
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptRequest(
            @PathVariable UUID requestId,
            @RequestHeader("X-auth-user") String mechanicEmail) {
        requestService.acceptRequest(requestId, mechanicEmail);
        return ResponseEntity.ok(ApiResponse.success("Request accepted", null));
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectRequest(
            @PathVariable UUID requestId,
            @RequestHeader("X-auth-user") String mechanicEmail) {
        requestService.rejectRequest(requestId, mechanicEmail);
        return ResponseEntity.ok(ApiResponse.success("Request rejected", null));
    }
}