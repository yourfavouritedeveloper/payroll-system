package org.example.employeems.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.dto.response.SalaryResponse;
import org.example.employeems.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KafkaConsumer {

    static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    final ObjectMapper objectMapper;
    final EmployeeService employeeService;



    @RetryableTopic(attempts = "1", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "${kafka.topics.salary}")
    public void consume(String message) throws JsonProcessingException {
        LOGGER.info("Received Message: {}", message);
        SalaryResponse salaryResponse = objectMapper.readValue(message, SalaryResponse.class);
        employeeService.updateBalance(salaryResponse.getEmployeeId(), salaryResponse.getSalary());

    }

    @DltHandler
    public void handleDltPayment(String message) {
        LOGGER.info("Event on dlt message: {}", message);
    }
}
