package com.shared.security;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.shared.security.exception.AuthenticationException;

public class AuthMiddleware {

    private AuthMiddleware() {
        throw new UnsupportedOperationException("Esta es una clase utilitaria y no puede ser instanciada");
    }

    public static String authenticate(HttpRequestMessage<?> request) {
        String authHeader = extractAuthorizationHeader(request);

        if (authHeader == null || authHeader.isEmpty()) {
            throw new AuthenticationException("Falta el header Authorization");
        }

        String username = JwtFilter.validate(authHeader);
        if (username == null) {
            throw new AuthenticationException("Token inválido o expirado");
        }

        return username;
    }

    private static String extractAuthorizationHeader(HttpRequestMessage<?> request) {
        String authHeader = request.getHeaders().get("Authorization");
        return authHeader != null ? authHeader : request.getHeaders().get("authorization");
    }
}
