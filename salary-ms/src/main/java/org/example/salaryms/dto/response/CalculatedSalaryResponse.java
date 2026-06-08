package org.example.salaryms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculatedSalaryResponse {

    UUID id;

    UUID employeeId;

    BigDecimal calculatedSalary;

    Timestamp createdAt;

    Timestamp updatedAt;

}
