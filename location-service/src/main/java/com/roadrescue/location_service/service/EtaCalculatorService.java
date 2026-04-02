package com.roadrescue.location_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.roadrescue.location_service.dto.RouteEstimate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EtaCalculatorService {

    private static final double AVG_SPEED_KMH = 30.0;
    private static final int EARTH_RADIUS_KM = 6371;
    private final RestClient.Builder restClientBuilder;

    @Value("${maps.google.distance-matrix.base-url:https://maps.googleapis.com}")
    private String googleBaseUrl;

    @Value("${maps.google.distance-matrix.api-key:}")
    private String googleApiKey;

    public RouteEstimate estimateRoute(BigDecimal fromLat,
                                       BigDecimal fromLng,
                                       BigDecimal toLat,
                                       BigDecimal toLng) {
        if (StringUtils.hasText(googleApiKey)) {
            try {
                JsonNode response = restClientBuilder
                        .baseUrl(googleBaseUrl)
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/maps/api/distancematrix/json")
                                .queryParam("origins", fromLat + "," + fromLng)
                                .queryParam("destinations", toLat + "," + toLng)
                                .queryParam("key", googleApiKey)
                                .build())
                        .retrieve()
                        .body(JsonNode.class);

                JsonNode element = response.path("rows").path(0).path("elements").path(0);
                if ("OK".equalsIgnoreCase(response.path("status").asText())
                        && "OK".equalsIgnoreCase(element.path("status").asText())) {
                    double distanceKm = element.path("distance").path("value").asDouble() / 1000.0;
                    int etaMinutes = Math.max(1, (int) Math.ceil(element.path("duration").path("value").asDouble() / 60.0));
                    return RouteEstimate.builder()
                            .distanceKm(roundDistance(distanceKm))
                            .etaMinutes(etaMinutes)
                            .provider("GOOGLE_DISTANCE_MATRIX")
                            .build();
                }
            } catch (Exception exception) {
                log.warn("Google Distance Matrix lookup failed, falling back to Haversine ETA: {}", exception.getMessage());
            }
        }

        double distanceKm = calculateHaversineDistanceKm(fromLat, fromLng, toLat, toLng);
        return RouteEstimate.builder()
                .distanceKm(roundDistance(distanceKm))
                .etaMinutes(calculateEtaMinutes(distanceKm))
                .provider("HAVERSINE_FALLBACK")
                .build();
    }

    private double calculateHaversineDistanceKm(BigDecimal fromLat, BigDecimal fromLng,
                                                BigDecimal toLat, BigDecimal toLng) {
        double dLat = Math.toRadians(toLat.doubleValue() - fromLat.doubleValue());
        double dLng = Math.toRadians(toLng.doubleValue() - fromLng.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat.doubleValue()))
                * Math.cos(Math.toRadians(toLat.doubleValue()))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    private int calculateEtaMinutes(double distanceKm) {
        double hours = distanceKm / AVG_SPEED_KMH;
        return Math.max(1, (int) Math.ceil(hours * 60));
    }

    private double roundDistance(double distanceKm) {
        return Math.round(distanceKm * 100.0) / 100.0;
    }
}
