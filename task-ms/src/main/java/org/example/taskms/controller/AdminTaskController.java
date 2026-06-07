package org.example.taskms.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.dto.request.TaskRequest;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/task")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTaskController {

    TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(taskRequest));
    }
}
