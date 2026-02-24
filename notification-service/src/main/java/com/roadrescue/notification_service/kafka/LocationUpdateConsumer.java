package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.LocationUpdateEvent;
import com.roadrescue.notification_service.dto.NotificationType;
import com.roadrescue.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocationUpdateConsumer {

    private final NotificationService    notificationService;
    private final SimpMessagingTemplate  messagingTemplate;

    /** Threshold (km) that triggers the "arriving soon" push notification. */
    private static final double ARRIVING_SOON_THRESHOLD_KM = 0.5;

    @KafkaListener(
            topics   = "location-updates",
            groupId  = "notification-service-group"
    )
    public void consumeLocationUpdate(LocationUpdateEvent event) {
        log.info("Location update — request: {}, ETA: {} min, dist: {:.2f} km",
                event.getRequestId(), event.getEtaMinutes(), event.getDistanceRemainingKm());

        try {
            Map<String, Object> trackingData = new HashMap<>();
            trackingData.put("requestId",           event.getRequestId());
            trackingData.put("mechanicId",           event.getMechanicId());
            trackingData.put("mechanicLat",          event.getCurrentLat());
            trackingData.put("mechanicLng",          event.getCurrentLng());
            trackingData.put("etaMinutes",           event.getEtaMinutes());
            trackingData.put("distanceRemainingKm",  event.getDistanceRemainingKm());
            trackingData.put("timestamp",            event.getTimestamp());


            String trackingDest = "/topic/tracking/" + event.getRequestId();
            messagingTemplate.convertAndSend(trackingDest, Optional.of(trackingData));
            log.debug("Tracking data sent → {}", trackingDest);

            if (event.getDistanceRemainingKm() != null
                    && event.getDistanceRemainingKm() <= ARRIVING_SOON_THRESHOLD_KM) {

                notificationService.sendToCustomer(
                        event.getCustomerId(),
                        NotificationType.MECHANIC_ARRIVING_SOON,
                        "Mechanic Arriving Soon!",
                        "Your mechanic is less than 500 m away. Please be ready.",
                        trackingData
                );

                log.info("Arriving-soon alert sent to customer {}", event.getCustomerId());
            }

        } catch (Exception e) {
            log.error("Error processing location update for request {}: {}",
                    event.getRequestId(), e.getMessage(), e);
        }
    }
}