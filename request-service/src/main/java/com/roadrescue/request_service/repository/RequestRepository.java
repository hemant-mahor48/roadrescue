package com.roadrescue.request_service.repository;

import com.roadrescue.request_service.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    @Query("SELECT r FROM Request r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
    List<Request> findByUserId(@Param("userId") UUID userId);
}
