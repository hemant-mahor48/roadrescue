package com.roadrescue.request_service.repository;

import com.roadrescue.request_service.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
}
