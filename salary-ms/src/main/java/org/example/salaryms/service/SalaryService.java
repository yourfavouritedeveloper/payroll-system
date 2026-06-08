package org.example.salaryms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.entity.CalculatedSalary;
import org.example.salaryms.entity.Salary;
import org.example.salaryms.repository.CalculatedSalaryRepository;
import org.example.salaryms.repository.SalaryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalaryService {

    SalaryRepository salaryRepository;
    CalculatedSalaryRepository calculatedSalaryRepository;

    @Scheduled(cron = "0 0 19 */30 * *")
    public void payoff() {
        List<CalculatedSalary> calculatedSalaries = calculatedSalaryRepository.findAll();
        calculatedSalaries.forEach(calculatedSalary -> {
            Salary salary = Salary.builder()
                    .employeeId(calculatedSalary.getEmployeeId())
                    .salary(calculatedSalary.getCalculatedSalary())
                    .paymentDate(Timestamp.from(Instant.now()))
                    .build();

            salaryRepository.save(salary);
            calculatedSalary.setCalculatedSalary(BigDecimal.ZERO);
            calculatedSalaryRepository.save(calculatedSalary);
        });
    }


}
