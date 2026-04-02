package com.roadrescue.notification_service.kafka;

import com.roadrescue.notification_service.dto.NotificationType;
import com.roadrescue.notification_service.dto.ReviewEvent;
import com.roadrescue.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.topic.reviews-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ReviewEvent event) {
        notificationService.sendToMechanic(
                event.getMechanicId(),
                NotificationType.RATING_RECEIVED,
                "You received a new review",
                String.format("You received a %d-star review%s",
                        event.getRating(),
                        event.getReview() == null || event.getReview().isBlank() ? "!" : "."),
                event
        );

        log.info("Sent review notification to mechanic {} for request {}",
                event.getMechanicId(), event.getRequestId());
    }
}
