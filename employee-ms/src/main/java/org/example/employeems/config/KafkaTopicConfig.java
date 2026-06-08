package org.example.employeems.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaTopicConfig {

    @Value("${kafka.topics.employee}")
    String employeeTopic;

    @Bean
    public NewTopic employeeTopic() {
        return TopicBuilder.name(employeeTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

