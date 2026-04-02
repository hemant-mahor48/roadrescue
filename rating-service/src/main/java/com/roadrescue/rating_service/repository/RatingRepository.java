package com.roadrescue.rating_service.repository;

import com.roadrescue.rating_service.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByRequestId(UUID requestId);
    long countByMechanicId(UUID mechanicId);

    @Query("select avg(r.score) from Rating r where r.mechanicId = :mechanicId")
    Double calculateAverageScoreByMechanicId(@Param("mechanicId") UUID mechanicId);
}
