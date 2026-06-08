package org.example.taskms.exception;

public class CredentialsDontMatchException extends RuntimeException {
    public CredentialsDontMatchException(String message) {
        super(message);
    }
}
