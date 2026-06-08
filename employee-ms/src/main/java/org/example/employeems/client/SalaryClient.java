package org.example.employeems.client;

import org.example.employeems.dto.response.CalculatedSalaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(name = "salary-ms",
        url = "${salary.service.url}",
        configuration = FeignClientInterceptor.class)
public interface SalaryClient {

    @PostMapping("/api/admin/calculated-salary/{employeeId}")
    CalculatedSalaryResponse createSalaryCalculator(@PathVariable UUID employeeId);

}
