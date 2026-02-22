package com.auth.domain.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    
    private final String username;
    
    public UserAlreadyExistsException(String username) {
        super("El usuario ya existe: " + username);
        this.username = username;
    }
    
    public UserAlreadyExistsException(String username, Throwable cause) {
        super("El usuario ya existe: " + username, cause);
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}