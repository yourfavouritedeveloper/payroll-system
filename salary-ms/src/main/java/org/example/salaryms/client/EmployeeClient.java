package org.example.salaryms.client;

import org.example.salaryms.dto.response.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


@FeignClient(name = "employee-ms",
        url = "${employee.service.url}",
        configuration = FeignClientInterceptor.class)
public interface EmployeeClient {

    @GetMapping("/api/employee/fin/{finCode}")
    EmployeeResponse findByFinCode(@PathVariable String finCode);


    @GetMapping("/api/employee/id/{id}")
    EmployeeResponse findById(@PathVariable UUID id);

}
