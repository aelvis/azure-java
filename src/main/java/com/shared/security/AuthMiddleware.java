package com.shared.security;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.shared.security.exception.AuthenticationException;
import com.shared.utils.DbContext;
import com.auth.domain.entities.UserEntity;
import com.auth.domain.entities.RoleEntity;
import com.auth.domain.repositories.UserRepositoryImpl;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthMiddleware {

    private AuthMiddleware() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }
    public static String authenticate(HttpRequestMessage<?> request, String... requiredRoles) {
        String username = authenticate(request);
        
        if (requiredRoles == null || requiredRoles.length == 0) {
            return username;
        }
        
        try (DbContext db = new DbContext()) {
            UserRepositoryImpl userRepo = new UserRepositoryImpl(db.em());
            
            UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado: " + username));
            
            Set<String> userRoles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
            
            boolean hasRequiredRole = Arrays.stream(requiredRoles)
                .anyMatch(userRoles::contains);
            
            if (!hasRequiredRole) {
                String required = String.join(", ", requiredRoles);
                String userHas = String.join(", ", userRoles);
                throw new AuthenticationException(
                    String.format("Acceso denegado. Roles requeridos: [%s]. Roles del usuario: [%s]", 
                        required, userHas)
                );
            }
            
            return username;
            
        }
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