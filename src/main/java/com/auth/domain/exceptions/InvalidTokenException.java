package com.auth.domain.exceptions;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException(String message) {
        super(message);
    }
}