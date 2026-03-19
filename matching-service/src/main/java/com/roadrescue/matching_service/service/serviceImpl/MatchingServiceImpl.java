package com.roadrescue.matching_service.service.serviceImpl;

import com.roadrescue.matching_service.client.LocationFeignClient;
import com.roadrescue.matching_service.client.UserFeignClient;
import com.roadrescue.matching_service.dto.*;
import com.roadrescue.matching_service.service.KafkaProducerService;
import com.roadrescue.matching_service.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final LocationFeignClient locationClient;
    private final UserFeignClient userClient;
    private final KafkaProducerService kafkaProducerService;

    @Value("${matching.search-radius-km:10.0}")
    private Double searchRadiusKm;

    @Override
    public UUID findBestMechanic(BreakdownRequestEvent event) {
        log.info("Finding mechanic for request: {}", event.getRequestId());

        List<NearbyMechanic> nearbyMechanics = locationClient.findNearbyMechanics(
                event.getLatitude(),
                event.getLongitude(),
                searchRadiusKm
        );

        if (nearbyMechanics.isEmpty()) {
            log.warn("No mechanics found for request: {}", event.getRequestId());
            // TODO: Publish 'no-mechanic-found' event to trigger Notification Service (suggest towing) and Analytics
            // kafkaProducerService.sendNoMechanicFoundEvent(event);
            return null;
        }

        // Step 2: Calculate match scores and select best
        NearbyMechanic bestMechanic = nearbyMechanics.stream()
                .map(mechanic -> {
                    Double score = calculateMatchScore(mechanic, event);
                    log.debug("Mechanic {} score: {}", mechanic.getMechanicId(), score);
                    return new MechanicWithScore(mechanic, score);
                })
                .max(Comparator.comparing(MechanicWithScore::getScore))
                .map(MechanicWithScore::getMechanic)
                .orElse(null);

        if (bestMechanic == null) {
            return null;
        }

        // Step 3: Publish notification event
        MechanicNotificationEvent notificationEvent = MechanicNotificationEvent.builder()
                .requestId(event.getRequestId())
                .mechanicId(bestMechanic.getMechanicId())
                .customerId(event.getUserId())
                .customerLatitude(event.getLatitude())
                .customerLongitude(event.getLongitude())
                .estimatedDistance(bestMechanic.getDistance())
                .issueType(event.getIssueType())
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducerService.sendMechanicNotification(notificationEvent);

        log.info("Matched mechanic {} for request {}",
                bestMechanic.getMechanicId(), event.getRequestId());

        return bestMechanic.getMechanicId();
    }
    @Override
    public Double calculateMatchScore(NearbyMechanic mechanic, BreakdownRequestEvent event) {
        try {
            // Fetch mechanic profile
            MechanicProfileDTO profile = userClient.getMechanicProfile(mechanic.getMechanicId()).getData();

            // Weight factors
            double distanceWeight = 0.40;
            double ratingWeight = 0.30;
            double specializationWeight = 0.20;
            double acceptanceRateWeight = 0.10;

            // Distance score (inverse - closer is better)
            double distanceScore = 1.0 - (mechanic.getDistance() / Math.max(searchRadiusKm, 1.0));
            distanceScore = Math.max(0, distanceScore);

            // Rating score (0-5 → 0-1)
            double ratingScore = profile.getRating() / 5.0;

            // Specialization match
            double specializationScore = matchesSpecialization(
                    profile.getSpecialization(),
                    event.getIssueType()) ? 1.0 : 0.5;

            // Acceptance rate score
            double acceptanceScore = profile.getAcceptanceRate() / 100.0;

            // Calculate weighted score
            return (distanceScore * distanceWeight) +
                    (ratingScore * ratingWeight) +
                    (specializationScore * specializationWeight) +
                    (acceptanceScore * acceptanceRateWeight);

        } catch (Exception e) {
            log.error("Error calculating score for mechanic: {}", mechanic.getMechanicId(), e);
            // Fallback to distance-only scoring
            return Math.max(0, 1.0 - (mechanic.getDistance() / Math.max(searchRadiusKm, 1.0)));
        }
    }

    private boolean matchesSpecialization(String specialization, com.roadrescue.matching_service.model.IssueType issueType) {
        if (specialization == null || issueType == null) {
            return false;
        }

        return switch (issueType) {
            case TYRE_PUNCTURE -> specialization.contains("TYRE");
            case BATTERY_ISSUE -> specialization.contains("BATTERY");
            case ENGINE_FAILURE -> specialization.contains("ENGINE");
            case FUEL_EMPTY -> specialization.contains("FUEL");
            default -> true; // General mechanics can handle other issues
        };
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class MechanicWithScore {
        private NearbyMechanic mechanic;
        private Double score;
    }
}
