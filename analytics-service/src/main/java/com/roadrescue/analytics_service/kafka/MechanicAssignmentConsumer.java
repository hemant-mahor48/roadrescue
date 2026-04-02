package com.roadrescue.analytics_service.kafka;

import com.roadrescue.analytics_service.dto.MechanicAssignmentEvent;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MechanicAssignmentConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "${spring.kafka.topic.mechanic-assignment-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(MechanicAssignmentEvent event) {
        analyticsService.recordMechanicAssignment(event);
        log.info("Recorded mechanic assignment analytics for request {}", event.getRequestId());
    }
}
