package org.example.taskms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskms.enumeration.Role;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private UUID id;
    private String finCode;
    private String fullName;
    private Role role;
    private BigDecimal salaryPerTask;
}
