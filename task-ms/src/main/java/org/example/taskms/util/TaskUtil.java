package org.example.taskms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.client.EmployeeClient;
import org.example.taskms.dto.response.EmployeeResponse;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.entity.Task;
import org.example.taskms.enumeration.Role;
import org.example.taskms.exception.EmployeeNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskUtil {

    EmployeeClient employeeClient;

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .employeeId(task.getEmployeeId())
                .title(task.getTitle())
                .description(task.getDescription())
                .severity(task.getSeverity())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .status(task.getStatus())
                .build();
    }

    public Boolean isValid (String finCode, UUID id) {
        EmployeeResponse employeeResponse = employeeClient.findByFinCode(finCode);

        return employeeResponse.getId().equals(id) || employeeResponse.getRole() == Role.ADMIN;
    }

}
