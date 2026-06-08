package org.example.taskms.exception;

public class TaskLimitReachedException extends RuntimeException {
    public TaskLimitReachedException(String message) {
        super(message);
    }
}
