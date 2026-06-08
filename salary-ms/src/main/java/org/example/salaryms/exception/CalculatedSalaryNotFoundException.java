package org.example.salaryms.exception;

public class CalculatedSalaryNotFoundException extends RuntimeException {
    public CalculatedSalaryNotFoundException(String message) {
        super(message);
    }
}
