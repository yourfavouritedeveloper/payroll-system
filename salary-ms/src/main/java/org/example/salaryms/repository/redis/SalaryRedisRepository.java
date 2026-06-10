package org.example.salaryms.repository.redis;

import lombok.RequiredArgsConstructor;
import org.example.salaryms.entity.CalculatedSalary;
import org.example.salaryms.entity.Salary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SalaryRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String SALARY_HASH_KEY = "salary:";
    private static final String SALARY_KEY_PREFIX = "salary:employee:";

    public Salary save(Salary salary) {
        template.opsForHash().put(SALARY_HASH_KEY, salary.getId().toString(), salary);

        template.opsForValue().set(
                SALARY_KEY_PREFIX + salary.getEmployeeId(),
                salary.getId().toString()
        );

        return salary;
    }

    public List<Salary> findAll() {
        return template.opsForHash()
                .values(SALARY_HASH_KEY)
                .stream()
                .map(obj -> (Salary) obj)
                .collect(Collectors.toList());
    }

    public Optional<Salary> findById(UUID id) {
        Object salary = template.opsForHash().get(SALARY_HASH_KEY, id.toString());
        return Optional.ofNullable((Salary) salary);
    }

    public Optional<List<Salary>> findByEmployee(UUID employeeId) {

        return Optional.of(findAll().stream()
                .filter(task -> task.getEmployeeId() == employeeId)
                .collect(Collectors.toList()));
    }

    public void delete(UUID id) {
        Object salaryObj = template.opsForHash().get(SALARY_HASH_KEY, id.toString());

        if (salaryObj != null) {
            Salary salary = (Salary) salaryObj;
            template.delete(SALARY_KEY_PREFIX + salary.getEmployeeId());
        }

        template.opsForHash().delete(SALARY_HASH_KEY, id.toString());
    }
}
