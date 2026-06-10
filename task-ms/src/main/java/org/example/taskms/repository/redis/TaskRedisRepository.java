package org.example.taskms.repository.redis;

import lombok.RequiredArgsConstructor;
import org.example.taskms.entity.Task;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String TASK_HASH_KEY = "task:";
    private static final String TASK_KEY_PREFIX = "task:employee:";

    public Task save(Task task) {
        template.opsForHash().put(TASK_HASH_KEY, task.getId().toString(), task);

        template.opsForValue().set(
                TASK_KEY_PREFIX + task.getEmployeeId(),
                task.getId().toString()
        );

        return task;
    }

    public List<Task> findAll() {
        return template.opsForHash()
                .values(TASK_HASH_KEY)
                .stream()
                .map(obj -> (Task) obj)
                .collect(Collectors.toList());
    }

    public Optional<Task> findById(UUID id) {
        Object task = template.opsForHash().get(TASK_HASH_KEY, id.toString());
        return Optional.ofNullable((Task) task);
    }

    public Optional<List<Task>> findByEmployee(UUID employeeId) {


        return Optional.of(findAll().stream()
                .filter(task -> task.getEmployeeId() == employeeId)
                .collect(Collectors.toList()));
    }

    public void delete(UUID id) {
        Object taskObj = template.opsForHash().get(TASK_HASH_KEY, id.toString());

        if (taskObj != null) {
            Task task = (Task) taskObj;
            template.delete(TASK_KEY_PREFIX + task.getEmployeeId());
        }

        template.opsForHash().delete(TASK_HASH_KEY, id.toString());
    }
}
