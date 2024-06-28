package com.service.user.exceptions.exceptionHandler;

import com.service.user.exceptions.UserNotFoundException;
import com.service.user.exceptions.WrongUserCredentials;
import com.service.user.model.exception.ErrorResponseMessage;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponseMessage handleUserNotFound(UserNotFoundException e){
        return new ErrorResponseMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }


    @ExceptionHandler(value = WrongUserCredentials.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponseMessage handleWrongCredentials(WrongUserCredentials e){
        return new ErrorResponseMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponseMessage handleBadCredentials(BadCredentialsException e){
        return new ErrorResponseMessage(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(value = LockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponseMessage handleLockedAccount(LockedException e){
        return new ErrorResponseMessage(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponseMessage handleExpiredJwtException(ExpiredJwtException e){
        return new ErrorResponseMessage(HttpStatus.FORBIDDEN.value(), e.getMessage());
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponseMessage handleExpiredJwtException(HttpClientErrorException e){
        return new ErrorResponseMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
