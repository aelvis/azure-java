package com.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    
    @NotBlank(message = "El nombre del usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre del usuarios debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}