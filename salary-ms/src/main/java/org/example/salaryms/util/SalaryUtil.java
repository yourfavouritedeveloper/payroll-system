package org.example.salaryms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.client.EmployeeClient;
import org.example.salaryms.dto.response.EmployeeResponse;
import org.example.salaryms.dto.response.SalaryResponse;
import org.example.salaryms.entity.Salary;
import org.example.salaryms.enumeration.Role;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalaryUtil {


    public SalaryResponse toSalaryResponse(Salary salary) {
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployeeId())
                .paymentDate(salary.getPaymentDate())
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .salary(salary.getSalary())
                .build();
    }

}
