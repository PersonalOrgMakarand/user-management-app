package com.example.usermanagement.exception;

public class TooManyLoginAttemptsException extends RuntimeException {

    public TooManyLoginAttemptsException() {
        super("Too many login attempts. Please try again later.");
    }
}
