package com.service.user.exceptions;

public class WrongUserCredentials extends RuntimeException{

    public WrongUserCredentials (String message) {
        super(message);
    }
}
