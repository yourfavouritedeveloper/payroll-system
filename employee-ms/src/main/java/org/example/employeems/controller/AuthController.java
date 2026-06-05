package org.example.employeems.controller;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.dto.request.LoginRequest;
import org.example.employeems.dto.response.AuthResponse;
import org.example.employeems.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {


    EmployeeService employeeService;


    @PutMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(employeeService.authenticate(loginRequest));
    }

    @PutMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        employeeService.logout(token);
        return ResponseEntity.ok("Successfully logged out");
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String token) {
        return ResponseEntity.ok(employeeService.refresh(token));
    }
}
