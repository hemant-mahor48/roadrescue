package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.ReviewAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewAnalyticsRepository extends MongoRepository<ReviewAnalytics, String> {
    long countBy();
}
