package org.example.employeems.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.dto.request.RegisterRequest;
import org.example.employeems.dto.response.EmployeeResponse;
import org.example.employeems.entity.Employee;
import org.example.employeems.enumeration.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeUtil {

    PasswordEncoder passwordEncoder;

    public Employee toEmployee(RegisterRequest registerRequest) {
        return Employee.builder()
                .finCode(registerRequest.getFinCode())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .role(Role.USER)
                .officialSalary(registerRequest.getOfficialSalary())
                .build();
    }

    public EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .finCode(employee.getFinCode())
                .fullName(employee.getFullName())
                .role(Role.USER)
                .build();
    }
}
