package com.roadrescue.notification_service.service;

import com.roadrescue.notification_service.dto.NotificationType;

import java.util.UUID;

public interface NotificationService {

    /**
     * Send notification to a specific user
     */
    void sendNotification(UUID recipientId, String recipientType,
                          NotificationType type, String title,
                          String message, Object data);

    /**
     * Send notification to customer
     */
    void sendToCustomer(UUID customerId, NotificationType type,
                        String title, String message, Object data);

    /**
     * Send notification to mechanic
     */
    void sendToMechanic(UUID mechanicId, NotificationType type,
                        String title, String message, Object data);

    /**
     * Send push notification (for mobile apps)
     */
    void sendPushNotification(UUID recipientId, String title, String message);
}
