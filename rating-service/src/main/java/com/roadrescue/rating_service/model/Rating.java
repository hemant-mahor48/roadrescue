package com.roadrescue.rating_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID requestId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID mechanicId;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 1000)
    private String review;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
