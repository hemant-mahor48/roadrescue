package com.roadrescue.matching_service.service;

import com.roadrescue.matching_service.dto.MechanicNotificationEvent;

public interface KafkaProducerService {
    void sendMechanicNotification(MechanicNotificationEvent event);
}
