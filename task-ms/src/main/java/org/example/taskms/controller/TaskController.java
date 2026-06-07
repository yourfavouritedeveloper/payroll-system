package org.example.taskms.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {

    TaskService taskService;

    @GetMapping("/{employeeId}")
    public ResponseEntity<List<TaskResponse>> findFinishedTasksByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(taskService.findFinishedTasksByEmployeeId(employeeId));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.startTask(id));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TaskResponse> finishTask(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.finishTask(id));
    }
}
