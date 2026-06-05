package org.example.employeems.exception;

import org.example.employeems.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(EmployeeNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(InvalidValidationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValidationException(InvalidValidationException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException e) {
        return buildResponse(e.getMessage(), 400);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, status));
    }

}
