package com.service;

import com.security.JwtUtil;
import com.security.PasswordEncoderProvider;
import com.domain.entities.UserEntity;

import jakarta.persistence.EntityManager;

public class AuthService {

    private final UserService userService;

    public AuthService(EntityManager em) {
        this.userService = new UserService(em);
    }

    public String login(String username, String password) {
        UserEntity user = userService.findByUsername(username);

        if (user == null) return null;

        if (!PasswordEncoderProvider.get().matches(password, user.getPassword())) {
            return null;
        }

        return JwtUtil.generateToken(username);
    }
}
