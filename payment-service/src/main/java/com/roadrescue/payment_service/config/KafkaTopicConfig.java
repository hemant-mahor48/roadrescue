package com.roadrescue.payment_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.service-completion-topic}")
    private String serviceCompletionTopic;

    @Value("${spring.kafka.topic.payments-topic}")
    private String paymentsTopic;

    @Bean
    public NewTopic serviceCompletionTopic() {
        return TopicBuilder.name(serviceCompletionTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name(paymentsTopic).partitions(3).replicas(1).build();
    }
}
