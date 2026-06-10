package org.example.salaryms.repository.redis;

import lombok.RequiredArgsConstructor;
import org.example.salaryms.entity.CalculatedSalary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CalculatedSalaryRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String SALARY_HASH_KEY = "calculated-salary:";
    private static final String SALARY_KEY_PREFIX = "calculated-salary:employee:";

    public CalculatedSalary save(CalculatedSalary calculatedSalary) {
        template.opsForHash().put(SALARY_HASH_KEY, calculatedSalary.getId().toString(), calculatedSalary);

        template.opsForValue().set(
                SALARY_KEY_PREFIX + calculatedSalary.getEmployeeId(),
                calculatedSalary.getId().toString()
        );

        return calculatedSalary;
    }

    public List<CalculatedSalary> findAll() {
        return template.opsForHash()
                .values(SALARY_HASH_KEY)
                .stream()
                .map(obj -> (CalculatedSalary) obj)
                .collect(Collectors.toList());
    }

    public Optional<CalculatedSalary> findById(UUID id) {
        Object calculatedSalary = template.opsForHash().get(SALARY_HASH_KEY, id.toString());
        return Optional.ofNullable((CalculatedSalary) calculatedSalary);
    }

    public Optional<CalculatedSalary> findByEmployeeId(UUID employeeId) {
        Object salaryId = template.opsForValue()
                .get(SALARY_KEY_PREFIX + employeeId);

        if (salaryId == null) {
            return Optional.empty();
        }

        return findById(UUID.fromString(salaryId.toString()));
    }

    public void delete(UUID id) {
        Object calculatedSalaryObj = template.opsForHash().get(SALARY_HASH_KEY, id.toString());

        if (calculatedSalaryObj != null) {
            CalculatedSalary calculatedSalary = (CalculatedSalary) calculatedSalaryObj;
            template.delete(SALARY_KEY_PREFIX + calculatedSalary.getEmployeeId());
        }

        template.opsForHash().delete(SALARY_HASH_KEY, id.toString());
    }
}
