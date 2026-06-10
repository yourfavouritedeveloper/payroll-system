package org.example.employeems.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.config.JwtService;
import org.example.employeems.dto.request.RegisterRequest;
import org.example.employeems.dto.response.AuthResponse;
import org.example.employeems.dto.response.EmployeeResponse;
import org.example.employeems.entity.Employee;
import org.example.employeems.entity.RefreshToken;
import org.example.employeems.enumeration.Role;
import org.example.employeems.repository.jpa.RefreshTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeUtil {

    PasswordEncoder passwordEncoder;
    RefreshTokenRepository refreshTokenRepository;
    JwtService jwtService;

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
                .salaryPerTask(employee.getSalaryPerTask())
                .build();
    }


    public AuthResponse returnToken(Employee employee) {
        String accessToken = jwtService.generateAccessToken(employee);
        String refreshToken = jwtService.generateRefreshToken(employee);

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .employee(employee)
                .role(employee.getRole())
                .build();

        refreshTokenRepository.save(token);


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
