package com.roadrescue.request_service.kafka;

import com.roadrescue.request_service.dto.PaymentEvent;
import com.roadrescue.request_service.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final RequestService requestService;

    @KafkaListener(
            topics = "${spring.kafka.topic.payments-topic}",
            groupId = "request-service-group"
    )
    public void consumePaymentEvent(PaymentEvent event) {
        log.info("Received payment event for request {}", event.getRequestId());
        requestService.handlePaymentUpdate(event);
    }
}
