package com.roadrescue.matching_service.service.serviceImpl;

import com.roadrescue.matching_service.dto.MechanicNotificationEvent;
import com.roadrescue.matching_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.mechanic-notification-topic}")
    private String topic;

    @Override
    public void sendMechanicNotification(MechanicNotificationEvent event) {
        kafkaTemplate.send(topic, event.getMechanicId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Mechanic notification sent: {}", event);
                    } else {
                        log.error("Failed to send mechanic notification", ex);
                    }
                });
    }
}
