package com.notifier.email.exception;

public class NotificationNotFound extends RuntimeException{
    public NotificationNotFound(String message) {
        super(message);
    }
}
