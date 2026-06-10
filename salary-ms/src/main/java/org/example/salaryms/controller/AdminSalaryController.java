package org.example.salaryms.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.config.JwtService;
import org.example.salaryms.dto.response.SalaryResponse;
import org.example.salaryms.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/admin/salary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminSalaryController {

    SalaryService salaryService;
    JwtService jwtService;

    @GetMapping("/date")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SalaryResponse>> findByDate(@RequestHeader("Authorization") String token,
                                                            @RequestParam Timestamp date) {
        return ResponseEntity.ok(salaryService.findByDate(jwtService.extractUsername(token.substring(7)),date,null));
    }
}
