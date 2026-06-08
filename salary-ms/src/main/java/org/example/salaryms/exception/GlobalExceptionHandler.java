package org.example.salaryms.exception;

import org.example.salaryms.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CalculatedSalaryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCalculatedSalaryNotFoundException(CalculatedSalaryNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(InsufficientSalaryException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientSalaryException(InsufficientSalaryException e) {
        return buildResponse(e.getMessage(), 404);
    }


    private ResponseEntity<ErrorResponse> buildResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, status));
    }

}
