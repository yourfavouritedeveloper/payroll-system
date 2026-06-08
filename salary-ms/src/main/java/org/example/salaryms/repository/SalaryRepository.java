package org.example.salaryms.repository;

import org.example.salaryms.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, UUID> {
}
