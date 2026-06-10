package org.example.salaryms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.salaryms.client.EmployeeClient;
import org.example.salaryms.dto.response.CalculatedSalaryResponse;
import org.example.salaryms.dto.response.EmployeeResponse;
import org.example.salaryms.dto.response.SalaryEventResponse;
import org.example.salaryms.service.CalculatedSalaryService;
import org.example.salaryms.service.SalaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KafkaConsumer {

    static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
    final ObjectMapper objectMapper;
    final CalculatedSalaryService calculatedSalaryService;

    @Value("${bind.pass.key}")
    String passKey;

    @RetryableTopic(attempts = "1", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "${kafka.topics.employee}")
    public void consumeEmployee(String message) throws JsonProcessingException {
        LOGGER.info("Received Message: {}", message);
        EmployeeResponse employeeResponse = objectMapper.readValue(message, EmployeeResponse.class);
        calculatedSalaryService.createSalaryCalculator(employeeResponse.getId());
    }

    @RetryableTopic(attempts = "1", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = "${kafka.topics.task}")
    public void consumeTask(String message) throws JsonProcessingException {
        LOGGER.info("Received Message: {}", message);
        SalaryEventResponse salaryEventResponse = objectMapper.readValue(message, SalaryEventResponse.class);
        calculatedSalaryService.updateCalculatedSalary(passKey,salaryEventResponse.getEmployeeId(), salaryEventResponse.getCalculatedSalary());
    }


    @DltHandler
    public void handleDltPayment(String message) {
        LOGGER.info("Event on dlt message: {}", message);
    }
}
