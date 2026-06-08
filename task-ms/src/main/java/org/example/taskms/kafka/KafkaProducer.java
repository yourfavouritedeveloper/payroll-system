package org.example.taskms.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducer {

    static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${kafka.topics.task}")
    String taskTopic;

    final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String message) {
        LOGGER.info("Sending message to topic {}: {}", taskTopic, message);
        kafkaTemplate.send(taskTopic, key, message);
    }
}
