package com.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

public class JwtUtil {

    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final long EXPIRATION = Long.parseLong(System.getenv("JWT_EXPIRATION_MS"));
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private JwtUtil() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
