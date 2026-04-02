package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.NotificationType;
import com.roadrescue.notification_service.dto.ServiceCompletionEvent;
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
public class ServiceCompletionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.service-completion-topic}",
            groupId = "notification-service-group"
    )
    public void consumeServiceCompletion(ServiceCompletionEvent event) {
        log.info("Received service completion event for request {}", event.getRequestId());

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", event.getRequestId());
        data.put("mechanicId", event.getMechanicId());
        data.put("serviceDurationMins", event.getServiceDurationMins());
        data.put("partsUsed", event.getPartsUsed());
        data.put("laborCharge", event.getLaborCharge());
        data.put("partsCharge", event.getPartsCharge());
        data.put("totalAmount", event.getTotalAmount());

        notificationService.sendToCustomer(
                event.getCustomerId(),
                NotificationType.SERVICE_COMPLETED,
                "Service completed",
                String.format("Your service is complete. Please pay Rs %.0f to close the request.", event.getTotalAmount()),
                data
        );

        notificationService.sendPushNotification(
                event.getCustomerId(),
                "Service completed",
                String.format("Please review and pay Rs %.0f", event.getTotalAmount())
        );
    }
}
