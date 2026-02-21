package com.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityConfig {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static PasswordEncoder passwordEncoder() {
        return encoder;
    }
}
