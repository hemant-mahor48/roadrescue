package com.roadrescue.analytics_service.kafka;

import com.roadrescue.analytics_service.dto.ReviewEvent;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "${spring.kafka.topic.reviews-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ReviewEvent event) {
        analyticsService.recordReview(event);
        log.info("Recorded review analytics for request {}", event.getRequestId());
    }
}
