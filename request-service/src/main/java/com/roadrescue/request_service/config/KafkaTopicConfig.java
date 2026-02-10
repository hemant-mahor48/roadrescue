package com.roadrescue.request_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.breakdown-request-topic}")
    private String topicName;

    @Value("${spring.kafka.topic.mechanic-assignment-topic}")
    private String mechanicAssignmentTopic;

    @Value("${spring.kafka.topic.mechanic-rejection-topic}")
    private String mechanicRejectionTopic;

    @Bean
    public NewTopic mechanicAssignmentTopic() {
        return TopicBuilder.name(mechanicAssignmentTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic mechanicRejectionTopic() {
        return TopicBuilder.name(mechanicRejectionTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic breakdownRequestsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

