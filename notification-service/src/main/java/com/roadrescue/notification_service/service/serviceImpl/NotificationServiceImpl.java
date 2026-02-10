package com.roadrescue.notification_service.service.serviceImpl;

import com.roadrescue.notification_service.dto.Notification;
import com.roadrescue.notification_service.dto.NotificationType;
import com.roadrescue.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotification(UUID recipientId, String recipientType,
                                 NotificationType type, String title,
                                 String message, Object data) {

        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .recipientId(recipientId)
                .recipientType(recipientType)
                .type(type)
                .title(title)
                .message(message)
                .data(data)
                .read(false)
                .timestamp(LocalDateTime.now())
                .build();

        // Send to specific user via WebSocket
        String destination = "/queue/notifications/" + recipientType.toLowerCase()
                + "/" + recipientId;

        messagingTemplate.convertAndSend(destination, notification);

        log.info("Notification sent to {} {}: {} - {}",
                recipientType, recipientId, type, title);
    }

    @Override
    public void sendToCustomer(UUID customerId, NotificationType type,
                               String title, String message, Object data) {
        sendNotification(customerId, "CUSTOMER", type, title, message, data);
    }

    @Override
    public void sendToMechanic(UUID mechanicId, NotificationType type,
                               String title, String message, Object data) {
        sendNotification(mechanicId, "MECHANIC", type, title, message, data);
    }

    @Override
    public void sendPushNotification(UUID recipientId, String title, String message) {
        // TODO: Implement Firebase Cloud Messaging (FCM) or similar
        // For now, just log
        log.info("Push notification to {}: {} - {}", recipientId, title, message);

        // Example FCM implementation:
        // fcmService.sendNotification(recipientId, title, message);
    }
}