package org.example.employeems.repository.jpa;

import org.example.employeems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByFinCode(String finCode);
}
