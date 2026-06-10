package org.example.salaryms.config;

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

    @Value("${kafka.topics.salary}")
    String salaryTopic;

    @Bean
    public NewTopic salaryTopic() {
        return TopicBuilder.name(salaryTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}

