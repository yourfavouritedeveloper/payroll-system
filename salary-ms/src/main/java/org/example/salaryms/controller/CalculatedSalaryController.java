package org.example.salaryms.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.service.CalculatedSalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/calculated-salary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CalculatedSalaryController {

    CalculatedSalaryService calculatedSalaryService;

    @PutMapping("/{employeeId}")
    public ResponseEntity<CalculatedSalaryResponse> updateCalculatedSalary(@PathVariable UUID employeeId, @RequestParam BigDecimal calculatedSalary){
        return ResponseEntity.ok(calculatedSalaryService.updateCalculatedSalary(employeeId, calculatedSalary));
    }
}
