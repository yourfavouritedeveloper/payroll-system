package org.example.salaryms.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.service.CalculatedSalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/calculated-salary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCalculatedSalaryController {

    CalculatedSalaryService calculatedSalaryService;

    @PostMapping("/{employeeId}")
    public ResponseEntity<CalculatedSalaryResponse> createSalaryCalculator(@PathVariable UUID employeeId){
        return ResponseEntity.ok(calculatedSalaryService.createSalaryCalculator(employeeId));
    }
}
