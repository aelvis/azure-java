package com.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.config.SecurityConfig;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PasswordEncoderProvider {
    
    private static final Logger LOGGER = Logger.getLogger(PasswordEncoderProvider.class.getName());
    private static PasswordEncoder passwordEncoderInstance;
    private static final PasswordEncoder DEFAULT_ENCODER = new BCryptPasswordEncoder();
    
    private PasswordEncoderProvider() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }
    
    static {
        initializeEncoder();
    }
    
    private static void initializeEncoder() {
        try {
            PasswordEncoder encoder = SecurityConfig.passwordEncoder();
            if (encoder != null) {
                passwordEncoderInstance = encoder;
                LOGGER.log(Level.INFO, "PasswordEncoder inicializado correctamente");
            } else {
                LOGGER.log(Level.WARNING, "SecurityConfig.passwordEncoder() retornó null, usando encoder por defecto");
                passwordEncoderInstance = DEFAULT_ENCODER;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar PasswordEncoder: {0}", e.getMessage());
            passwordEncoderInstance = DEFAULT_ENCODER;
        }
    }

    public static PasswordEncoder get() {
        return passwordEncoderInstance;
    }
    
    public static Optional<PasswordEncoder> getOptional() {
        return Optional.ofNullable(passwordEncoderInstance);
    }
    
    static void reset() {
        initializeEncoder();
    }
}