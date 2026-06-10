package org.example.salaryms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.entity.CalculatedSalary;
import org.example.salaryms.exception.CalculatedSalaryNotFoundException;
import org.example.salaryms.exception.CredentialsDontMatchException;
import org.example.salaryms.exception.InsufficientSalaryException;
import org.example.salaryms.repository.jpa.CalculatedSalaryRepository;
import org.example.salaryms.repository.redis.CalculatedSalaryRedisRepository;
import org.example.salaryms.util.CalculatedSalaryUtil;
import org.example.salaryms.util.JwtBindUtil;
import org.example.salaryms.util.SalaryUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculatedSalaryService {

    CalculatedSalaryRepository calculatedSalaryRepository;
    CalculatedSalaryRedisRepository calculatedSalaryRedisRepository;
    CalculatedSalaryUtil calculatedSalaryUtil;
    JwtBindUtil jwtBindUtil;


    @Transactional
    public CalculatedSalaryResponse createSalaryCalculator(UUID employeeId){
        CalculatedSalary calculatedSalary = CalculatedSalary.builder()
                .employeeId(employeeId)
                .calculatedSalary(BigDecimal.ZERO)
                .build();

        calculatedSalaryRepository.save(calculatedSalary);
        calculatedSalaryRedisRepository.save(calculatedSalary);

        calculatedSalaryRepository.flush();

        return calculatedSalaryUtil.toCalculatedSalaryResponse(calculatedSalary);
    }

    @Transactional
    public CalculatedSalaryResponse updateCalculatedSalary(String finCode, UUID employeeId, BigDecimal salary){
        CalculatedSalary calculatedSalary = calculatedSalaryRedisRepository.findByEmployeeId(employeeId)
                        .orElseGet(() -> calculatedSalaryRepository.findByEmployeeId(employeeId)
                                .orElseThrow(() -> new CalculatedSalaryNotFoundException("Calculated salary not found")));

        if(salary.compareTo(BigDecimal.ZERO) <= 0){
            throw new InsufficientSalaryException("Insufficient salary");
        }

        if(!jwtBindUtil.isValid(finCode, employeeId)) {
            throw new CredentialsDontMatchException("Invalid credentials");
        }

        calculatedSalary.setCalculatedSalary(calculatedSalary.getCalculatedSalary().add(salary));
        calculatedSalaryRepository.save(calculatedSalary);
        calculatedSalaryRedisRepository.save(calculatedSalary);
        calculatedSalaryRepository.flush();

        return calculatedSalaryUtil.toCalculatedSalaryResponse(calculatedSalary);
    }
}
