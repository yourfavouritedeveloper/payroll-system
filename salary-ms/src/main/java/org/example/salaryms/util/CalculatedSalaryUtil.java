package org.example.salaryms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.entity.CalculatedSalary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculatedSalaryUtil {

    public CalculatedSalaryResponse toCalculatedSalaryResponse(CalculatedSalary calculatedSalary) {
        return CalculatedSalaryResponse.builder()
                .calculatedSalary(calculatedSalary.getCalculatedSalary())
                .id(calculatedSalary.getId())
                .createdAt(calculatedSalary.getCreatedAt())
                .updatedAt(calculatedSalary.getUpdatedAt())
                .employeeId(calculatedSalary.getEmployeeId())
                .build();
    }
}
