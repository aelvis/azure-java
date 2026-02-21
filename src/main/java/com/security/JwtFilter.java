package com.security;

public class JwtFilter {

    public static String validate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        try {
            return JwtUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }
}
