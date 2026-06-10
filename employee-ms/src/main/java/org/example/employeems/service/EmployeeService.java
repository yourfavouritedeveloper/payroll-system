package org.example.employeems.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.config.JwtService;
import org.example.employeems.dto.request.LoginRequest;
import org.example.employeems.dto.request.RegisterRequest;
import org.example.employeems.dto.response.AuthResponse;
import org.example.employeems.dto.response.EmployeeResponse;
import org.example.employeems.entity.Employee;
import org.example.employeems.entity.OutboxEvent;
import org.example.employeems.entity.RefreshToken;
import org.example.employeems.enumeration.OutboxStatus;
import org.example.employeems.exception.EmployeeNotFoundException;
import org.example.employeems.exception.InvalidValidationException;
import org.example.employeems.exception.PasswordDoesNotMatchException;
import org.example.employeems.exception.RefreshTokenNotFoundException;
import org.example.employeems.repository.jpa.EmployeeRepository;
import org.example.employeems.repository.jpa.OutboxEventRepository;
import org.example.employeems.repository.jpa.RefreshTokenRepository;
import org.example.employeems.repository.redis.EmployeeRedisRepository;
import org.example.employeems.util.EmployeeUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {

    EmployeeRepository employeeRepository;
    EmployeeRedisRepository employeeRedisRepository;
    RefreshTokenRepository refreshTokenRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;
    EmployeeUtil employeeUtil;
    ObjectMapper objectMapper;
    OutboxEventRepository outboxEventRepository;


    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) throws JsonProcessingException {
        Employee employee = employeeUtil.toEmployee(registerRequest);
        employeeRepository.save(employee);
        employeeRedisRepository.save(employee);
        employeeRepository.flush();

        EmployeeResponse employeeResponse = employeeUtil.toEmployeeResponse(employee);

        OutboxEvent event = OutboxEvent.builder()
                .topic("employee.created")
                .key(employee.getId().toString())
                .value(objectMapper.writeValueAsString(employeeResponse))
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        return employeeUtil.returnToken(employee);

    }

    @Transactional
    public AuthResponse authenticate(LoginRequest loginRequest) {

        Employee employee = employeeRedisRepository.findByFinCode(loginRequest.getFinCode())
                .orElseGet(() -> employeeRepository.findByFinCode(loginRequest.getFinCode())
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist")));

        if(!passwordEncoder.matches(loginRequest.getPassword(), employee.getPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match");
        }


        return employeeUtil.returnToken(employee);
    }


    @Transactional
    public void logout(String token) {

        String finCode = jwtService.extractUsername(token);

        Employee employee = employeeRedisRepository.findByFinCode(finCode)
                .orElseGet(() -> employeeRepository.findByFinCode(finCode)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist")));

        if (!jwtService.validateToken(token, employee)) {
            throw new InvalidValidationException("Invalid token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByEmployee(employee)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token with provided employee does not exist"));

        refreshTokenRepository.delete(refreshToken);


    }

    @Transactional
    public EmployeeResponse updateBalance(UUID id, BigDecimal newBalance) {
        Employee employee = employeeRedisRepository.findById(id)
                .orElseGet(() -> employeeRepository.findById(id)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist")));


        employee.setBalance(newBalance);
        employeeRepository.save(employee);
        employeeRedisRepository.save(employee);
        return employeeUtil.toEmployeeResponse(employee);
    }


    @Transactional
    public AuthResponse refresh(String token) {

        String finCode = jwtService.extractUsername(token);

        Employee employee = employeeRedisRepository.findByFinCode(finCode)
                .orElseGet(() -> employeeRepository.findByFinCode(finCode)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist")));

        RefreshToken refreshToken = refreshTokenRepository.findByEmployee(employee)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));


        if (!jwtService.validateToken(refreshToken.getRefreshToken(), employee)) {
            throw new InvalidValidationException("Invalid token");
        }

        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();

        return employeeUtil.returnToken(employee);


    }


    @Transactional(readOnly = true)
    public EmployeeResponse findByFinCode(String finCode) {
        Employee employee = employeeRedisRepository.findByFinCode(finCode)
                .orElseGet(() -> employeeRepository.findByFinCode(finCode)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given fin code does not exist")));

        return employeeUtil.toEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(UUID id) {
        Employee employee = employeeRedisRepository.findById(id)
                .orElseGet(() -> employeeRepository.findById(id)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee with given id does not exist")));

        return employeeUtil.toEmployeeResponse(employee);

    }





}
