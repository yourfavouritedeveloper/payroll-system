package org.example.salaryms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.salaryms.entity.OutboxEvent;
import org.example.salaryms.enumeration.OutboxStatus;
import org.example.salaryms.kafka.KafkaProducer;
import org.example.salaryms.repository.jpa.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxScheduler {

    OutboxEventRepository outboxEventRepository;
    KafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaProducer.sendMessage(event.getKey(), event.getValue());
                event.setStatus(OutboxStatus.PUBLISHED);
                outboxEventRepository.save(event);
                log.info("Outbox event published: {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event: {}", event.getId(), e);
            }
        }
    }
}
