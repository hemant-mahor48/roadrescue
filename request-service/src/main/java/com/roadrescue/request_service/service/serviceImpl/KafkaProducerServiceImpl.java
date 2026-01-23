package com.roadrescue.request_service.service.serviceImpl;

import com.roadrescue.request_service.dto.BreakdownRequestEvent;
import com.roadrescue.request_service.service.KafkaProducerService;
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

    @Value("${spring.kafka.topic.breakdown-request-topic}")
    private String topic;

    @Override
    public void sendEvent(BreakdownRequestEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.getRequestId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully: {}", event);
                    } else {
                        log.error("Failed to send message", ex);
                    }
                });
    }
}
