package org.example.salaryms.exception;

public class InsufficientSalaryException extends RuntimeException {
    public InsufficientSalaryException(String message) {
        super(message);
    }
}
