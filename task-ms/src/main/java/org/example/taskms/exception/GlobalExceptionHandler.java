package org.example.taskms.exception;

import org.example.taskms.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTaskStatusException(InvalidTaskStatusException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(CredentialsDontMatchException.class)
    public ResponseEntity<ErrorResponse> handleCredentialsDontMatchException(CredentialsDontMatchException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(TaskLimitReachedException.class)
    public ResponseEntity<ErrorResponse> handleTaskLimitReachedException(TaskLimitReachedException e) {
        return buildResponse(e.getMessage(), 404);
    }



    private ResponseEntity<ErrorResponse> buildResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, status));
    }

}
