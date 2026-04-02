package com.roadrescue.payment_service.kafka;

import com.roadrescue.payment_service.dto.MechanicAssignmentEvent;
import com.roadrescue.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MechanicAssignmentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${spring.kafka.topic.mechanic-assignment-topic}",
            groupId = "payment-service-group"
    )
    public void consumeMechanicAssignment(MechanicAssignmentEvent event) {
        log.info("Received mechanic assignment for request {}", event.getRequestId());
        paymentService.createEstimatedPaymentHold(event);
    }
}
