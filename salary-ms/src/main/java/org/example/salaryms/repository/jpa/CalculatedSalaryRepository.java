package org.example.salaryms.repository.jpa;

import org.example.salaryms.entity.CalculatedSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CalculatedSalaryRepository extends JpaRepository<CalculatedSalary, UUID> {
    Optional<CalculatedSalary> findByEmployeeId(UUID employeeId);
}
