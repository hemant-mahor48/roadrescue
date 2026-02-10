package com.roadrescue.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private UUID id;
    private UUID recipientId;
    private String recipientType; // CUSTOMER or MECHANIC
    private NotificationType type;
    private String title;
    private String message;
    private Object data; // Additional structured data
    private boolean read;
    private LocalDateTime timestamp;
}
