package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.MechanicAssignmentEvent;
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
public class MechanicAssignmentConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.mechanic-assignment-topic}",
            groupId = "notification-service-group"
    )
    public void consumeMechanicAssignment(MechanicAssignmentEvent event) {
        log.info("Received mechanic assignment event for request: {}, mechanic: {}",
                event.getRequestId(), event.getMechanicId());

        try {
            // Prepare notification data
            Map<String, Object> data = new HashMap<>();
            data.put("requestId", event.getRequestId());
            data.put("mechanicId", event.getMechanicId());
            data.put("status", event.getStatus());
            data.put("assignedAt", event.getAssignedAt());

            // Send notification to customer
            if (event.getCustomerId() != null) {
                notificationService.sendToCustomer(
                        event.getCustomerId(),
                        NotificationType.MECHANIC_ASSIGNED,
                        "Mechanic Assigned!",
                        "A verified mechanic has been assigned to your request and is on the way.",
                        data
                );

                // Also send push notification
                notificationService.sendPushNotification(
                        event.getCustomerId(),
                        "Mechanic Assigned",
                        "Your mechanic is on the way!"
                );
            }

            // Send confirmation to mechanic
            notificationService.sendToMechanic(
                    event.getMechanicId(),
                    NotificationType.REQUEST_ACCEPTED,
                    "Request Accepted",
                    "You have accepted the service request. Please proceed to the location.",
                    data
            );

            log.info("Notifications sent for mechanic assignment: {}", event.getRequestId());

        } catch (Exception e) {
            log.error("Error processing mechanic assignment event: {}",
                    event.getRequestId(), e);
        }
    }
}
