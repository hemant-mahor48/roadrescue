package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.PaymentAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentAnalyticsRepository extends MongoRepository<PaymentAnalytics, String> {
    long countByStatus(String status);
}
