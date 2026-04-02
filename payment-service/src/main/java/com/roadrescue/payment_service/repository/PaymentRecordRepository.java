package com.roadrescue.payment_service.repository;

import com.roadrescue.payment_service.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, UUID> {
    Optional<PaymentRecord> findByRequestId(UUID requestId);
}
