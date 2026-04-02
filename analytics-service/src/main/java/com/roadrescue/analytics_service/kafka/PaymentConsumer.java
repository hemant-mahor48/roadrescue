package com.roadrescue.analytics_service.kafka;

import com.roadrescue.analytics_service.dto.PaymentEvent;
import com.roadrescue.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${spring.kafka.topic.payments-topic}",
            groupId = "analytics-service-group"
    )
    public void consumePayment(PaymentEvent event) {
        log.info("Analytics received payment for request {}", event.getRequestId());
        analyticsService.recordPayment(event);
    }
}
