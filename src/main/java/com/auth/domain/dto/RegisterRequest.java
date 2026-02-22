package com.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username solo puede contener letras, números, puntos, guiones y guiones bajos")
    private String username;

    @NotBlank(message = "Password es requerido")
    @Size(min = 8, max = 100, message = "Password debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password debe contener al menos: 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial")
    private String password;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "Email no puede exceder 100 caracteres")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}