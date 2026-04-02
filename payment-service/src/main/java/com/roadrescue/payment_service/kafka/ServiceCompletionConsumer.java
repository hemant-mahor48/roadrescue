package com.roadrescue.payment_service.kafka;

import com.roadrescue.payment_service.dto.ServiceCompletionEvent;
import com.roadrescue.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceCompletionConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${spring.kafka.topic.service-completion-topic}",
            groupId = "payment-service-group"
    )
    public void consumeServiceCompletion(ServiceCompletionEvent event) {
        log.info("Received service completion for request {}", event.getRequestId());
        paymentService.createPendingPayment(event);
    }
}
