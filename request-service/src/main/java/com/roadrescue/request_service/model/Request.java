package com.roadrescue.request_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private List<UUID> vehicleIds;

    @Column(nullable = false)
    private BigDecimal locationLatitude;
    @Column(nullable = false)
    private BigDecimal locationLongitude;

    private String address;

    @Enumerated(EnumType.STRING)
    private IssueType issueType;
    @Column(length = 1000)
    private String description;

    private UUID mechanicId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ElementCollection
    private List<String> partsUsed;

    private Double laborCharge;
    private Double partsCharge;
    private Double finalAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
