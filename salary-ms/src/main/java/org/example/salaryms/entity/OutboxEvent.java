package org.example.salaryms.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.enumeration.OutboxStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "outbox_events")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false)
    String topic;

    @Column(nullable = false)
    String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    String value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    OutboxStatus status;

    @CreationTimestamp
    Timestamp createdAt;

    @UpdateTimestamp
    Timestamp updatedAt;
}
