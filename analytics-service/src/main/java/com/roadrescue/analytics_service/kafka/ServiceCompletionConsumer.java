package com.roadrescue.analytics_service.kafka;

import com.roadrescue.analytics_service.dto.ServiceCompletionEvent;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceCompletionConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${spring.kafka.topic.service-completion-topic}",
            groupId = "analytics-service-group"
    )
    public void consumeServiceCompletion(ServiceCompletionEvent event) {
        log.info("Analytics received service completion for request {}", event.getRequestId());
        analyticsService.recordServiceCompletion(event);
    }
}
