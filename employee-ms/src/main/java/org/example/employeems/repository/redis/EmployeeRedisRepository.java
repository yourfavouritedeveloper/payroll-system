package org.example.employeems.repository.redis;

import lombok.RequiredArgsConstructor;
import org.example.employeems.entity.Employee;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmployeeRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String EMPLOYEE_HASH_KEY = "employee:";
    private static final String EMPLOYEE_KEY_PREFIX = "employee:fin:";

    public Employee save(Employee employee) {
        template.opsForHash().put(EMPLOYEE_HASH_KEY, employee.getId().toString(), employee);

        template.opsForValue().set(
                EMPLOYEE_KEY_PREFIX + employee.getFinCode(),
                employee.getId().toString()
        );

        return employee;
    }

    public List<Employee> findAll() {
        return template.opsForHash()
                .values(EMPLOYEE_HASH_KEY)
                .stream()
                .map(obj -> (Employee) obj)
                .collect(Collectors.toList());
    }

    public Optional<Employee> findById(UUID id) {
        Object employee = template.opsForHash().get(EMPLOYEE_HASH_KEY, id.toString());
        return Optional.ofNullable((Employee) employee);
    }

    public Optional<Employee> findByFinCode(String finCode) {
        Object employeeId = template.opsForValue()
                .get(EMPLOYEE_KEY_PREFIX + finCode);

        if (employeeId == null) {
            return Optional.empty();
        }

        return findById(UUID.fromString(employeeId.toString()));
    }

    public void delete(UUID id) {
        Object employeeOjb = template.opsForHash().get(EMPLOYEE_HASH_KEY, id.toString());

        if (employeeOjb != null) {
            Employee employee = (Employee) employeeOjb;
            template.delete(EMPLOYEE_KEY_PREFIX + employee.getFinCode());
        }

        template.opsForHash().delete(EMPLOYEE_HASH_KEY, id.toString());
    }
}
