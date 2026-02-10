package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.BreakdownRequestEvent;
import com.roadrescue.notification_service.dto.NotificationType;
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
public class BreakdownRequestConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.breakdown-request-topic}",
            groupId = "notification-service-group"
    )
    public void consumeBreakdownRequest(BreakdownRequestEvent event) {
        log.info("Received breakdown request event: {}", event.getRequestId());

        try {
            // Prepare notification data
            Map<String, Object> data = new HashMap<>();
            data.put("requestId", event.getRequestId());
            data.put("issueType", event.getIssueType());
            data.put("latitude", event.getLatitude());
            data.put("longitude", event.getLongitude());

            // Send notification to customer
            notificationService.sendToCustomer(
                    event.getUserId(),
                    NotificationType.SEARCHING_MECHANIC,
                    "Finding Mechanic",
                    "We're searching for the nearest available mechanic for your "
                            + formatIssueType(event.getIssueType()) + " issue.",
                    data
            );

            // Also send push notification
            notificationService.sendPushNotification(
                    event.getUserId(),
                    "Request Received",
                    "We're finding a mechanic near you..."
            );

        } catch (Exception e) {
            log.error("Error processing breakdown request event: {}",
                    event.getRequestId(), e);
        }
    }

    private String formatIssueType(String issueType) {
        if (issueType == null) return "vehicle";
        return issueType.replace("_", " ").toLowerCase();
    }
}
