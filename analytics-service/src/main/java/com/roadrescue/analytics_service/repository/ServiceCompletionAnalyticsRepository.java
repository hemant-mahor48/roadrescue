package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.ServiceCompletionAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ServiceCompletionAnalyticsRepository extends MongoRepository<ServiceCompletionAnalytics, String> {
    long countBy();
    long countByMechanicId(UUID mechanicId);
}
