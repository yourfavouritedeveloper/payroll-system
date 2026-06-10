package org.example.salaryms.repository.jpa;

import org.example.salaryms.entity.OutboxEvent;
import org.example.salaryms.enumeration.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
