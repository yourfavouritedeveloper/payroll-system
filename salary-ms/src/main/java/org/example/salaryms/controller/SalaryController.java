package org.example.salaryms.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.config.JwtService;
import org.example.salaryms.dto.response.SalaryResponse;
import org.example.salaryms.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalaryController {

    SalaryService salaryService;
    JwtService jwtService;

    @GetMapping("/id/{id}")
    public ResponseEntity<SalaryResponse> getById(@RequestHeader("Authorization") String token,
                                                  @PathVariable UUID id) {
        return ResponseEntity.ok(salaryService.findById(jwtService.extractUsername(token.substring(7)),id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SalaryResponse>> getByEmployeeId(@RequestHeader("Authorization") String token,
                                                                @PathVariable UUID employeeId) {
        return ResponseEntity.ok(salaryService.findByEmployee(jwtService.extractUsername(token.substring(7)),employeeId));
    }

    @GetMapping("/date/{employeeId}")
    public ResponseEntity<List<SalaryResponse>> getByDate(@RequestHeader("Authorization") String token,
                                                          @RequestParam Timestamp date, @PathVariable UUID employeeId) {
        return ResponseEntity.ok(salaryService.findByDate(jwtService.extractUsername(token.substring(7)),date,employeeId));
    }
}
