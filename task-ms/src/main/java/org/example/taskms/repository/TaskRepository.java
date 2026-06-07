package org.example.taskms.repository;

import org.example.taskms.entity.Task;
import org.example.taskms.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByEmployeeIdAndStatus(UUID employeeId, Status status);
}
