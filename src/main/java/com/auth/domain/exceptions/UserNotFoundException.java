package com.auth.domain.exceptions;

public class UserNotFoundException extends AuthException {
    private final String username;
    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
        this.username = username;
    }
    public String getUsername() { return username; }
}