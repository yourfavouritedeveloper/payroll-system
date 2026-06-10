package org.example.salaryms.exception;

public class CredentialsDontMatchException extends RuntimeException {
    public CredentialsDontMatchException(String message) {
        super(message);
    }
}
