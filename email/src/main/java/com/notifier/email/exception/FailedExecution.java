package com.notifier.email.exception;

public class FailedExecution extends RuntimeException{
    public FailedExecution(String message) {
        super(message);
    }
}
