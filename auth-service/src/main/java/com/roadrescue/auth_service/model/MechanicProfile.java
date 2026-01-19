package com.roadrescue.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mechanic_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MechanicProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Boolean isAvailable = false;

    @Column(precision = 10, scale = 8)
    private BigDecimal currentLocationLat;

    @Column(precision = 11, scale = 8)
    private BigDecimal currentLocationLng;

    private String vehicleNumber;
    private String licenseNumber;

    @Column(nullable = false)
    private Boolean aadharVerified = false;

    @Column(nullable = false)
    private Boolean policeVerificationDone = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
