package com.roadrescue.auth_service.kafka;

import com.roadrescue.auth_service.dto.ReviewEvent;
import com.roadrescue.auth_service.service.MechanicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumer {

    private final MechanicService mechanicService;

    @KafkaListener(topics = "${spring.kafka.topic.reviews-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeReviewEvent(ReviewEvent event) {
        mechanicService.updateRatingMetrics(
                event.getMechanicId(),
                event.getNewAvgRating(),
                event.getTotalReviews()
        );
        log.info("Updated mechanic {} rating to {} after review for request {}",
                event.getMechanicId(), event.getNewAvgRating(), event.getRequestId());
    }
}
