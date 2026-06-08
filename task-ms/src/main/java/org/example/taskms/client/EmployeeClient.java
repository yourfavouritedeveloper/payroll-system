package org.example.taskms.client;

import org.example.taskms.dto.response.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "employee-ms",
        url = "${employee.service.url}",
        configuration = FeignClientInterceptor.class)
public interface EmployeeClient {

    @GetMapping("/api/employee/{finCode}")
    EmployeeResponse findByFinCode(@PathVariable String finCode);
}
