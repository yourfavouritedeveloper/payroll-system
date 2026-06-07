package org.example.taskms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.taskms.enumeration.Status;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    UUID id;

    UUID employeeId;

    String title;

    String description;

    BigDecimal severity;

    Status status;

    Timestamp createdAt;

    Timestamp updatedAt;
}
