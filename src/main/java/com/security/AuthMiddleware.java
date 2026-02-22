package com.security;

import com.microsoft.azure.functions.HttpRequestMessage;

public class AuthMiddleware {

    public static String authenticate(HttpRequestMessage<?> request) {
        String authHeader = request.getHeaders().get("Authorization");
        if (authHeader == null) {
            authHeader = request.getHeaders().get("authorization");
        }
        if (authHeader == null || authHeader.isEmpty()) {
            throw new RuntimeException("Falta el header Authorization");
        }

        String username = JwtFilter.validate(authHeader);
        if (username == null) {
            throw new RuntimeException("Token inválido o expirado");
        }

        return username;
    }
}
