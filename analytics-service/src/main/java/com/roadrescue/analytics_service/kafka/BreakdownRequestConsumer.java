package com.roadrescue.analytics_service.kafka;

import com.roadrescue.analytics_service.dto.BreakdownRequestEvent;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BreakdownRequestConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${spring.kafka.topic.breakdown-request-topic}",
            groupId = "analytics-service-group"
    )
    public void consumeBreakdownRequest(BreakdownRequestEvent event) {
        log.info("Analytics received breakdown request {}", event.getRequestId());
        analyticsService.recordBreakdownRequest(event);
    }
}
