package com.roadrescue.location_service.service;

import com.roadrescue.location_service.dto.MechanicLocation;
import com.roadrescue.location_service.dto.NearbyMechanic;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LocationService {
    void updateMechanicLocation(UUID mechanicId, BigDecimal lat, BigDecimal lng);
    List<NearbyMechanic> findNearbyMechanics(BigDecimal lat, BigDecimal lng, Double radiusKm);
    void setMechanicAvailability(UUID mechanicId, Boolean isAvailable);
    MechanicLocation getMechanicLocation(UUID mechanicId);
}
