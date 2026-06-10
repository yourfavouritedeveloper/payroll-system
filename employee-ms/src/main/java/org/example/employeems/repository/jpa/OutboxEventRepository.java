package org.example.employeems.repository.jpa;

import org.example.employeems.entity.OutboxEvent;
import org.example.employeems.enumeration.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
