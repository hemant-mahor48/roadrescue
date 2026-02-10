package com.roadrescue.matching_service.kafka;

import com.roadrescue.matching_service.dto.BreakdownRequestEvent;
import com.roadrescue.matching_service.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MechanicRejectionConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "${spring.kafka.topic.mechanic-rejection-topic}",
            groupId = "matching-service-group"
    )
    public void handleMechanicRejection(BreakdownRequestEvent event) {
        log.info("Mechanic rejected request: {}, finding next mechanic",
                event.getRequestId());

        try {
            // Re-trigger matching for the next best mechanic
            matchingService.findBestMechanic(event);
        } catch (Exception e) {
            log.error("Error finding next mechanic for request: {}",
                    event.getRequestId(), e);
        }
    }
}
