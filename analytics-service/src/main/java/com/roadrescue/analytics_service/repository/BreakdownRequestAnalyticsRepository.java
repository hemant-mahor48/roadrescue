package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.BreakdownRequestAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface BreakdownRequestAnalyticsRepository extends MongoRepository<BreakdownRequestAnalytics, String> {
    long countBy();
    long countByIssueType(String issueType);
    Optional<BreakdownRequestAnalytics> findByRequestId(UUID requestId);
}
