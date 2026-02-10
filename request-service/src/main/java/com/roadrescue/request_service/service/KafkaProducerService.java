package com.roadrescue.request_service.service;

import com.roadrescue.request_service.dto.BreakdownRequestEvent;
import com.roadrescue.request_service.dto.MechanicAssignmentEvent;

public interface KafkaProducerService {
    void sendEvent(BreakdownRequestEvent event);
    void sendMechanicAssignmentEvent(MechanicAssignmentEvent event);
    void sendMechanicRejectionEvent(BreakdownRequestEvent event);
}
