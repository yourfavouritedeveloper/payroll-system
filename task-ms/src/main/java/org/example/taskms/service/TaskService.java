package org.example.taskms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.taskms.client.EmployeeClient;
import org.example.taskms.dto.request.TaskRequest;
import org.example.taskms.dto.response.EmployeeResponse;
import org.example.taskms.dto.response.SalaryEventResponse;
import org.example.taskms.dto.response.TaskResponse;
import org.example.taskms.entity.OutboxEvent;
import org.example.taskms.entity.Task;
import org.example.taskms.enumeration.OutboxStatus;
import org.example.taskms.enumeration.Status;
import org.example.taskms.exception.*;
import org.example.taskms.repository.jpa.OutboxEventRepository;
import org.example.taskms.repository.jpa.TaskRepository;
import org.example.taskms.repository.redis.TaskRedisRepository;
import org.example.taskms.util.TaskUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskService {

    TaskRepository taskRepository;
    TaskRedisRepository taskRedisRepository;
    TaskUtil taskUtil;
    EmployeeClient employeeClient;
    ObjectMapper objectMapper;
    OutboxEventRepository outboxEventRepository;

    @Transactional(readOnly = true)
    public TaskResponse findById(String finCode, UUID id) {

        Task task = taskRedisRepository.findById(id).orElseGet(
                () -> taskRepository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found")));


        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return taskUtil.toTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByEmployeeId(String finCode, UUID employeeId, Status status) {

        List<Task> tasks = taskRedisRepository.findByEmployee(employeeId).orElseGet(
                () -> taskRepository.findByEmployeeId(employeeId));

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
        taskRedisRepository.save(task);
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
        taskRedisRepository.save(task);
        return taskUtil.toTaskResponse(task);
    }

    @Transactional
    public TaskResponse finishTask(String finCode, UUID id) throws JsonProcessingException {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        if(task.getStatus() == Status.DONE) {
            throw new InvalidTaskStatusException("Task has already been finished");
        } else if(task.getStatus() != Status.IN_PROGRESS) {
            throw new InvalidTaskStatusException("You need to start a task first to finish it");
        }


        task.setStatus(Status.DONE);
        taskRepository.save(task);
        taskRedisRepository.save(task);
        taskRepository.flush();

        EmployeeResponse employeeResponse = employeeClient.findByFinCode(finCode);

        BigDecimal calculatedSalary = employeeResponse.getSalaryPerTask()
                .multiply(task.getSeverity());


        SalaryEventResponse salaryEventResponse = SalaryEventResponse.builder()
                .idempotencyKey(UUID.randomUUID())
                .calculatedSalary(calculatedSalary)
                .employeeId(task.getEmployeeId())
                .build();

        OutboxEvent event = OutboxEvent.builder()
                .topic("task.completed")
                .key(task.getId().toString())
                .value(objectMapper.writeValueAsString(salaryEventResponse))
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);



        return taskUtil.toTaskResponse(task);
    }


    public void deleteTask(String finCode, UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with the id " + id + " not found"));

        if(!taskUtil.isValid(finCode, task.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        taskRedisRepository.delete(task.getId());
        taskRepository.delete(task);
    }



    @Scheduled(cron = "0 0 19 */30 * *")
    public void deleteTasks() {
        List<Task> tasks = taskRedisRepository.findAll();
        tasks.stream()
                .filter(task -> task.getStatus() == Status.DONE)
                .forEach(taskRepository::delete);
    }


}
