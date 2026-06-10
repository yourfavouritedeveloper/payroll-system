package org.example.salaryms.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.client.EmployeeClient;
import org.example.salaryms.dto.response.EmployeeResponse;
import org.example.salaryms.enumeration.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtBindUtil {

    @Value("${bind.pass.key}")
    String passKey;

    final EmployeeClient employeeClient;


    public Boolean isValid (String finCode, UUID id) {
        EmployeeResponse employeeResponse = employeeClient.findByFinCode(finCode);

        return employeeResponse.getId().equals(id) || employeeResponse.getRole() == Role.ADMIN || finCode.equals(passKey);
    }

}
