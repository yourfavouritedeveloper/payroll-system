package org.example.salaryms.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.enumeration.Role;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {
    UUID idempotencyKey;
    UUID id;
    String finCode;
    String fullName;
    Role role;
    BigDecimal salaryPerTask;
}
