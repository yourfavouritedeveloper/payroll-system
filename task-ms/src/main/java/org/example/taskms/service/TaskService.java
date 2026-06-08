package org.example.taskms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.client.EmployeeClient;
import org.example.taskms.client.SalaryClient;
import org.example.taskms.dto.request.TaskRequest;
import org.example.taskms.dto.response.CalculatedSalaryResponse;
import org.example.taskms.dto.response.EmployeeResponse;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.entity.Task;
import org.example.taskms.enumeration.Status;
import org.example.taskms.exception.*;
import org.example.taskms.repository.TaskRepository;
import org.example.taskms.util.TaskUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;
    TaskUtil taskUtil;
    EmployeeClient employeeClient;
    SalaryClient salaryClient;

    @Transactional(readOnly = true)
    public TaskResponse findById(String finCode, UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return taskUtil.toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByEmployeeId(String finCode, UUID employeeId, Status status) {

        List<Task> tasks = taskRepository.findByEmployeeId(employeeId);

        if(!taskUtil.isValid(finCode, employeeId)) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return (status != null) ?
                tasks.stream()
                        .filter(task -> task.getStatus() == status)
                        .map(taskUtil::toTaskResponse)
                        .toList()
                : tasks.stream()
                        .map(taskUtil::toTaskResponse)
                        .toList();
    }

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest) {
        List<Task> tasks = taskRepository.findByEmployeeId(taskRequest.getEmployeeId());

        if(tasks.size() == 4) {
            throw new TaskLimitReachedException("Task limit reached");
        }


        Task task = Task.builder()
                .employeeId(taskRequest.getEmployeeId())
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .severity(taskRequest.getSeverity())
                .status(Status.TODO)
                .build();

        task = taskRepository.save(task);
        taskRepository.flush();
        return taskUtil.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse startTask(String finCode, UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        if(task.getStatus() != Status.TODO) {
            throw new InvalidTaskStatusException("Task has already been started");
        }

        task.setStatus(Status.IN_PROGRESS);
        task = taskRepository.save(task);
        return taskUtil.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse finishTask(String finCode, UUID id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        if(task.getStatus() == Status.DONE) {
            throw new InvalidTaskStatusException("Task has already been finished");
        }


        task.setStatus(Status.DONE);
        taskRepository.save(task);
        taskRepository.flush();

        EmployeeResponse employeeResponse = employeeClient.findByFinCode(finCode);

        BigDecimal calculatedSalary = employeeResponse.getSalaryPerTask()
                .multiply(task.getSeverity());

        salaryClient.updateCalculatedSalary(employeeResponse.getId(),calculatedSalary);


        return taskUtil.toTaskResponse(task);
    }


    public void deleteTask(String finCode, UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        taskRepository.delete(task);
    }



    @Scheduled(cron = "0 0 19 */30 * *")
    public void deleteTasks() {
        List<Task> tasks = taskRepository.findAll();
        tasks.stream()
                .filter(task -> task.getStatus() == Status.DONE)
                .forEach(taskRepository::delete);
    }


}
