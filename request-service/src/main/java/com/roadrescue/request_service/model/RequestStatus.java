package com.roadrescue.request_service.model;

public enum RequestStatus {
    PENDING,          // Created, not assigned
    SEARCHING,        // Matching in progress
    ASSIGNED,         // Mechanic assigned
    IN_PROGRESS,      // Mechanic arrived
    COMPLETED,        // Service completed
    CANCELLED,        // Cancelled
    PAYMENT_PENDING,  // Payment failed
    PAID              // Payment successful
}

