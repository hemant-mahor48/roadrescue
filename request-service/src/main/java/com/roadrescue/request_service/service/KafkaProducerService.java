package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequestEvent;

public interface KafkaProducerService {
    void sendEvent(BreakdownRequestEvent event);
}
