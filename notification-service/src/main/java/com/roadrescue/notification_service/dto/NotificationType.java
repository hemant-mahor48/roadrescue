package com.roadrescue.notification_service.dto;

public enum NotificationType {
    // Customer notifications
    REQUEST_CREATED,
    SEARCHING_MECHANIC,
    MECHANIC_ASSIGNED,
    MECHANIC_EN_ROUTE,
    MECHANIC_ARRIVED,
    SERVICE_STARTED,
    SERVICE_COMPLETED,
    PAYMENT_PENDING,
    PAYMENT_SUCCESS,
    NO_MECHANIC_FOUND,
    REQUEST_CANCELLED,

    // Mechanic notifications
    NEW_REQUEST_NEARBY,
    REQUEST_ACCEPTED,
    REQUEST_TIMEOUT,
    CUSTOMER_CANCELLED,
    PAYMENT_RECEIVED,
    RATING_RECEIVED,

    // Common
    SYSTEM_ALERT,
    CHAT_MESSAGE
}