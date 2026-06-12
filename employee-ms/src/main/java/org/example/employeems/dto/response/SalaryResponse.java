package org.example.employeems.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryResponse {

    UUID idempotencyKey;

    UUID id;

    UUID employeeId;

    BigDecimal salary;

    Timestamp paymentDate;

    Timestamp createdAt;

    Timestamp updatedAt;
}
