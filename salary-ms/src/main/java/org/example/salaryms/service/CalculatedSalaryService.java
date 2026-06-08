package org.example.salaryms.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.entity.CalculatedSalary;
import org.example.salaryms.exception.CalculatedSalaryNotFoundException;
import org.example.salaryms.exception.InsufficientSalaryException;
import org.example.salaryms.repository.CalculatedSalaryRepository;
import org.example.salaryms.util.CalculatedSalaryUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculatedSalaryService {

    CalculatedSalaryRepository calculatedSalaryRepository;
    CalculatedSalaryUtil calculatedSalaryUtil;

    @Transactional
    public CalculatedSalaryResponse createSalaryCalculator(UUID employeeId){
        CalculatedSalary calculatedSalary = CalculatedSalary.builder()
                .employeeId(employeeId)
                .calculatedSalary(BigDecimal.ZERO)
                .build();

        calculatedSalaryRepository.save(calculatedSalary);
        calculatedSalaryRepository.flush();

        return calculatedSalaryUtil.toCalculatedSalaryResponse(calculatedSalary);
    }

    @Transactional
    public CalculatedSalaryResponse updateCalculatedSalary(UUID employeeId, BigDecimal salary){
        CalculatedSalary calculatedSalary = calculatedSalaryRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CalculatedSalaryNotFoundException("Calculated salary not found"));

        if(salary.compareTo(BigDecimal.ZERO) <= 0){
            throw new InsufficientSalaryException("Insufficient salary");
        }

        calculatedSalary.setCalculatedSalary(calculatedSalary.getCalculatedSalary().add(salary));
        calculatedSalaryRepository.save(calculatedSalary);
        calculatedSalaryRepository.flush();

        return calculatedSalaryUtil.toCalculatedSalaryResponse(calculatedSalary);
    }
}
