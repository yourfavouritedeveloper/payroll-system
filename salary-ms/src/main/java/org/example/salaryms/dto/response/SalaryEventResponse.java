package org.example.salaryms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryEventResponse {

    UUID idempotencyKey;

    UUID employeeId;

    BigDecimal calculatedSalary;
}
