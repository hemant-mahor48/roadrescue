package com.roadrescue.location_service.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Calculates straight-line distance (Haversine) and ETA.
 * For production, replace calculateDistanceKm() with a Google Maps
 * Distance Matrix API call to get road-based routing.
 */
@Service
public class EtaCalculatorService {

    /** Assumed urban average speed in km/h. */
    private static final double AVG_SPEED_KMH = 30.0;
    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Returns straight-line distance in kilometres between two coordinate pairs.
     */
    public double calculateDistanceKm(BigDecimal fromLat, BigDecimal fromLng,
                                      BigDecimal toLat,   BigDecimal toLng) {

        double dLat = Math.toRadians(toLat.doubleValue()  - fromLat.doubleValue());
        double dLng = Math.toRadians(toLng.doubleValue()  - fromLng.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat.doubleValue()))
                * Math.cos(Math.toRadians(toLat.doubleValue()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Returns ETA in whole minutes, minimum 1.
     */
    public int calculateEtaMinutes(double distanceKm) {
        double hours = distanceKm / AVG_SPEED_KMH;
        return Math.max(1, (int) Math.ceil(hours * 60));
    }
}
