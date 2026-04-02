package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.NotificationType;
import com.roadrescue.notification_service.dto.PaymentEvent;
import com.roadrescue.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.payments-topic}",
            groupId = "notification-service-group"
    )
    public void consumePayment(PaymentEvent event) {
        log.info("Received payment event for request {}", event.getRequestId());

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", event.getRequestId());
        data.put("paymentId", event.getPaymentId());
        data.put("amount", event.getAmount());
        data.put("mechanicEarning", event.getMechanicEarning());
        data.put("platformFee", event.getPlatformFee());
        data.put("status", event.getStatus());

        notificationService.sendToCustomer(
                event.getCustomerId(),
                NotificationType.PAYMENT_SUCCESS,
                "Payment successful",
                String.format("Payment of Rs %.0f completed successfully.", event.getAmount()),
                data
        );

        notificationService.sendToMechanic(
                event.getMechanicId(),
                NotificationType.PAYMENT_RECEIVED,
                "Payment received",
                String.format("Customer payment received. Your earning is Rs %.0f.", event.getMechanicEarning()),
                data
        );

        notificationService.sendToCustomer(
                event.getCustomerId(),
                NotificationType.RATING_REQUEST,
                "Rate your mechanic",
                "Your payment is complete. Please rate your mechanic.",
                data
        );
    }
}
