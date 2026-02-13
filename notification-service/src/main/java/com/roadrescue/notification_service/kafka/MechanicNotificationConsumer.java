package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.MechanicNotificationEvent;
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
public class MechanicNotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.mechanic-notification-topic}",
            groupId = "notification-service-group"
    )
    public void consumeMechanicNotification(MechanicNotificationEvent event) {
        log.info("Received mechanic notification event for mechanic: {}",
                event.getMechanicId());

        try {
            // Prepare notification data
            Map<String, Object> data = new HashMap<>();
            data.put("requestId", event.getRequestId());
            data.put("customerLatitude", event.getCustomerLatitude());
            data.put("customerLongitude", event.getCustomerLongitude());
            data.put("estimatedDistance", event.getEstimatedDistance());
            data.put("issueType", event.getIssueType());

            // Format distance for display
            String distanceText = String.format("%.1f km", event.getEstimatedDistance());

            // Send notification to mechanic
            notificationService.sendToMechanic(
                    event.getMechanicId(),
                    NotificationType.NEW_REQUEST_NEARBY,
                    "New Request Nearby!",
                    "A customer needs help with "
                            + formatIssueType(String.valueOf(event.getIssueType()))
                            + ". Distance: " + distanceText,
                    data
            );

            // Send push notification
            notificationService.sendPushNotification(
                    event.getMechanicId(),
                    "New Request Nearby",
                    "Customer needs help " + distanceText + " away"
            );

        } catch (Exception e) {
            log.error("Error processing mechanic notification: {}",
                    event.getRequestId(), e);
        }
    }

    private String formatIssueType(String issueType) {
        if (issueType == null) return "vehicle issue";
        return issueType.replace("_", " ").toLowerCase();
    }
}