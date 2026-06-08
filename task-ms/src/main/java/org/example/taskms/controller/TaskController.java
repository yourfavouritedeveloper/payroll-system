package org.example.taskms.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.config.JwtService;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.enumeration.Status;
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
    JwtService jwtService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findTaskById(@RequestHeader("Authorization") String token,
                                                     @PathVariable UUID id) {
        return ResponseEntity.ok(taskService.findById(jwtService.extractUsername(token.substring(7)),id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskResponse>> findByEmployeeId(@RequestHeader("Authorization") String token,
                                                               @PathVariable UUID employeeId,
                                                               @RequestParam(required = false) Status status) {
        return ResponseEntity.ok(taskService.findByEmployeeId(jwtService.extractUsername(token.substring(7)), employeeId,status));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(@RequestHeader("Authorization") String token,
                                                  @PathVariable UUID id) {
        return ResponseEntity.ok(taskService.startTask(jwtService.extractUsername(token.substring(7)),id));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<TaskResponse> finishTask(@RequestHeader("Authorization") String token,
                                                   @PathVariable UUID id) {
        return ResponseEntity.ok(taskService.finishTask(jwtService.extractUsername(token.substring(7)),id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String token,
                                                   @PathVariable UUID id) {
        taskService.deleteTask(jwtService.extractUsername(token.substring(7)),id);
        return ResponseEntity.ok("Task deleted");
    }


}
