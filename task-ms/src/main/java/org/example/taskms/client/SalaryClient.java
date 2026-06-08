package org.example.taskms.client;

import org.example.taskms.dto.response.CalculatedSalaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "salary-ms",
        url = "${salary.service.url}",
        configuration = FeignClientInterceptor.class)
public interface SalaryClient {

    @PutMapping("/api/calculated-salary/{employeeId}")
    CalculatedSalaryResponse updateCalculatedSalary(@PathVariable UUID employeeId, @RequestParam BigDecimal calculatedSalary);

}
