package org.example.salaryms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.SalaryResponse;
import org.example.salaryms.entity.CalculatedSalary;
import org.example.salaryms.entity.OutboxEvent;
import org.example.salaryms.entity.Salary;
import org.example.salaryms.enumeration.OutboxStatus;
import org.example.salaryms.exception.CredentialsDontMatchException;
import org.example.salaryms.exception.SalaryNotFoundException;
import org.example.salaryms.repository.jpa.CalculatedSalaryRepository;
import org.example.salaryms.repository.jpa.OutboxEventRepository;
import org.example.salaryms.repository.jpa.SalaryRepository;
import org.example.salaryms.repository.redis.SalaryRedisRepository;
import org.example.salaryms.util.JwtBindUtil;
import org.example.salaryms.util.SalaryUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalaryService {

    SalaryRepository salaryRepository;
    SalaryRedisRepository salaryRedisRepository;
    CalculatedSalaryRepository calculatedSalaryRepository;
    OutboxEventRepository outboxEventRepository;
    ObjectMapper objectMapper;
    SalaryUtil salaryUtil;
    JwtBindUtil jwtBindUtil;


    @Transactional(readOnly = true)
    public SalaryResponse findById(String finCode, UUID id) {
        Salary salary = salaryRedisRepository.findById(id)
                .orElseGet(() -> salaryRepository.findById(id)
                        .orElseThrow(() -> new SalaryNotFoundException("Salary with id " + id + " not found")));

        if(!jwtBindUtil.isValid(finCode, salary.getEmployeeId())) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return salaryUtil.toSalaryResponse(salary);
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> findByEmployee(String finCode, UUID employeeId) {
        List<Salary> salaryList = salaryRedisRepository.findByEmployee(employeeId)
                .orElseGet(() -> salaryRepository.findByEmployeeId(employeeId));

        if(!jwtBindUtil.isValid(finCode, employeeId)) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return salaryList.stream()
                .map(salaryUtil::toSalaryResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> findByDate(String finCode, Timestamp date, UUID employeeId) {
        List<Salary> salaryList = salaryRedisRepository.findAll();

        if(employeeId != null && !jwtBindUtil.isValid(finCode, employeeId)) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        return salaryList.stream()
                .filter(salary -> (employeeId != null && salary.getEmployeeId().equals(employeeId)) || salary.getPaymentDate().after(date))
                .map(salaryUtil::toSalaryResponse)
                .collect(Collectors.toList());
    }


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
            salaryRepository.flush();

            SalaryResponse salaryResponse = salaryUtil.toSalaryResponse(salary);

            OutboxEvent event = OutboxEvent.builder()
                    .topic("salary.payoff")
                    .key(salaryResponse.getId().toString())
                    .value(objectMapper.writeValueAsString(salaryResponse))
                    .status(OutboxStatus.PENDING)
                    .build();

            outboxEventRepository.save(event);

            calculatedSalary.setCalculatedSalary(BigDecimal.ZERO);
            calculatedSalaryRepository.save(calculatedSalary);
        });
    }


}
