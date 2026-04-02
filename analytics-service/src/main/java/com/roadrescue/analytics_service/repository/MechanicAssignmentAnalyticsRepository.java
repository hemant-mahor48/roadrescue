package com.roadrescue.analytics_service.repository;

import com.roadrescue.analytics_service.model.MechanicAssignmentAnalytics;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MechanicAssignmentAnalyticsRepository extends MongoRepository<MechanicAssignmentAnalytics, String> {
    long countByMechanicId(UUID mechanicId);
    Optional<MechanicAssignmentAnalytics> findByRequestId(UUID requestId);

    @Aggregation(pipeline = {
            "{ $match: { mechanicId: ?0 } }",
            "{ $group: { _id: null, average: { $avg: '$responseTimeMins' } } }"
    })
    List<AverageResponseProjection> findAverageResponseTimeByMechanicId(UUID mechanicId);

    interface AverageResponseProjection {
        Double getAverage();
    }
}
