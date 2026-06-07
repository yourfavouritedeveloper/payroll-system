package org.example.employeems.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.config.JwtService;
import org.example.employeems.dto.request.LoginRequest;
import org.example.employeems.dto.request.RegisterRequest;
import org.example.employeems.dto.response.AuthResponse;
import org.example.employeems.entity.Employee;
import org.example.employeems.entity.RefreshToken;
import org.example.employeems.exception.EmployeeNotFoundException;
import org.example.employeems.exception.InvalidValidationException;
import org.example.employeems.exception.PasswordDoesNotMatchException;
import org.example.employeems.exception.RefreshTokenNotFoundException;
import org.example.employeems.repository.EmployeeRepository;
import org.example.employeems.repository.RefreshTokenRepository;
import org.example.employeems.util.EmployeeUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {

    EmployeeRepository employeeRepository;
    RefreshTokenRepository refreshTokenRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;
    EmployeeUtil employeeUtil;


    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        Employee employee = employeeUtil.toEmployee(registerRequest);
        employeeRepository.save(employee);

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

    @Transactional
    public AuthResponse authenticate(LoginRequest loginRequest) {
        Employee employee = (Employee) employeeRepository.findByFinCode(loginRequest.getFinCode())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match");
        }


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


    @Transactional
    public void logout(String token) {

        String finCode = jwtService.extractUsername(token);

        Employee employee = employeeRepository.findByFinCode(finCode)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        if (!jwtService.validateToken(token, employee)) {
            throw new InvalidValidationException("Invalid token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByEmployee(employee)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token with provided employee does not exist"));

        refreshTokenRepository.delete(refreshToken);


    }


    @Transactional
    public AuthResponse refresh(String token) {

        String finCode = jwtService.extractUsername(token);

        Employee employee = employeeRepository.findByFinCode(finCode)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        RefreshToken refreshToken = refreshTokenRepository.findByEmployee(employee)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));


        if (!jwtService.validateToken(refreshToken.getRefreshToken(), employee)) {
            throw new InvalidValidationException("Invalid token");
        }

        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();

        String newAccessToken = jwtService.generateAccessToken(employee);
        String newRefreshToken = jwtService.generateRefreshToken(employee);

        RefreshToken newToken = RefreshToken.builder()
                .refreshToken(newRefreshToken)
                .employee(employee)
                .role(employee.getRole())
                .build();

        refreshTokenRepository.save(newToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();


    }





}
