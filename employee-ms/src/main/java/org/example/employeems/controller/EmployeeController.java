package org.example.employeems.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.dto.response.EmployeeResponse;
import org.example.employeems.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {

    EmployeeService employeeService;

    @GetMapping("/fin/{finCode}")
    public ResponseEntity<EmployeeResponse> findByFinCode(@PathVariable String finCode) {
        return ResponseEntity.ok(employeeService.findByFinCode(finCode));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }


}
