package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.MechanicBadgeAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface MechanicBadgeAnalyticsRepository extends MongoRepository<MechanicBadgeAnalytics, String> {
    Optional<MechanicBadgeAnalytics> findByMechanicId(UUID mechanicId);
    long countByBadgesContaining(String badge);
}
