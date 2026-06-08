package org.example.taskms.config;

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

    @Value("${kafka.topics.task}")
    String taskTopic;

    @Bean
    public NewTopic taskTopic() {
        return TopicBuilder.name(taskTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

