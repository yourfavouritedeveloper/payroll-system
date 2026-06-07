package org.example.taskms.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class TaskRequest {

    @NotBlank(message = "Employee ID is required")
    UUID employeeId;

    @NotBlank(message = "task title is required")
    String title;

    @NotBlank(message = "task description is required")
    String description;

    @Min(1)
    @Max(5)
    BigDecimal severity;


}
