package org.example.salaryms.exception;

public class SalaryNotFoundException extends RuntimeException {
    public SalaryNotFoundException(String message) {
        super(message);
    }
}
