package org.example.taskms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.dto.request.TaskRequest;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.entity.Task;
import org.example.taskms.enumeration.Status;
import org.example.taskms.exception.InvalidTaskStatusException;
import org.example.taskms.exception.TaskNotFoundException;
import org.example.taskms.repository.TaskRepository;
import org.example.taskms.util.TaskUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;
    TaskUtil taskUtil;

    @Transactional(readOnly = true)
    public TaskResponse findById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        return taskUtil.toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findFinishedTasksByEmployeeId(UUID employeeId) {
        List<Task> tasks = taskRepository.findByEmployeeIdAndStatus(employeeId, Status.DONE);

        return tasks.stream()
                .map(taskUtil::toTaskResponse)
                .toList();
    }

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        Task task = Task.builder()
                .employeeId(taskRequest.getEmployeeId())
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .severity(taskRequest.getSeverity())
                .build();

        task = taskRepository.save(task);
        return taskUtil.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse startTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(task.getStatus() != Status.TODO) {
            throw new InvalidTaskStatusException("Task has already been started");
        }

        task.setStatus(Status.IN_PROGRESS);
        task = taskRepository.save(task);
        return taskUtil.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse finishTask(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(task.getStatus() == Status.DONE) {
            throw new InvalidTaskStatusException("Task has already been finished");
        }

        task.setStatus(Status.DONE);
        task = taskRepository.save(task);
        return taskUtil.toTaskResponse(task);
    }


}
