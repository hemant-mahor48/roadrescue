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
public class BreakdownRequestConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "${spring.kafka.topic.breakdown-request-topic}",
            groupId = "matching-service-group"
    )
    public void consumeBreakdownRequest(BreakdownRequestEvent event) {
        log.info("Received breakdown request: {}", event.getRequestId());

        try {
            matchingService.findBestMechanic(event);
        } catch (Exception e) {
            log.error("Error processing breakdown request: {}", event.getRequestId(), e);
        }
    }
}
