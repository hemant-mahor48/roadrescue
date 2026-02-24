package com.roadrescue.location_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "request-service")
public interface RequestServiceClient {

    @PutMapping("/api/v1/requests/{requestId}/en-route")
    void markEnRoute(@PathVariable UUID requestId);
}