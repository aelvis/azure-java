package com.shared.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityConfig {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    private SecurityConfig() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static PasswordEncoder passwordEncoder() {
        return encoder;
    }
}
