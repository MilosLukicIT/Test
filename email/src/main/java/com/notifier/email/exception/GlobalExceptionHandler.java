package com.notifier.email.exception;

import com.notifier.email.model.ErrorResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotificationNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponseMessage handleUserNotFound(NotificationNotFound e){
        return new ErrorResponseMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
