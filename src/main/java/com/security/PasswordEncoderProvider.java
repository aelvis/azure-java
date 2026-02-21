package com.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.config.SecurityConfig;

public class PasswordEncoderProvider {

    public static PasswordEncoder get() {
        return SecurityConfig.passwordEncoder();
    }
}
