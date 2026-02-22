package com.shared.security;

public class JwtFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    private JwtFilter() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static String validate(String authHeader) {
        if (!isValidBearerToken(authHeader)) {
            return null;
        }

        String token = authHeader.substring(BEARER_PREFIX_LENGTH);

        try {
            return JwtUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValidBearerToken(String authHeader) {
        return authHeader != null && authHeader.startsWith(BEARER_PREFIX);
    }
}
