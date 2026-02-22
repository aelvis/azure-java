package com.auth.domain.exceptions;

public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}