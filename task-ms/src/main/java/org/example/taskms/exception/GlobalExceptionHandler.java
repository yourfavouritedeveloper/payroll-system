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


    private ResponseEntity<ErrorResponse> buildResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, status));
    }

}
