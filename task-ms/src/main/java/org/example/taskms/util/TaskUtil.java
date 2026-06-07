package org.example.taskms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.entity.Task;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskUtil {

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
}
