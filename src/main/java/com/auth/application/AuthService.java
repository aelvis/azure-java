package com.auth.application;

import com.auth.domain.entities.UserEntity;
import com.auth.domain.exceptions.InvalidCredentialsException;
import com.auth.domain.exceptions.UserNotFoundException;
import com.shared.security.JwtUtil;
import com.shared.security.PasswordEncoderProvider;


public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public String login(String username, String password) {
        UserEntity user = userService.findByUsername(username);

        if (user == null) {
            throw new UserNotFoundException(username);
        }

        if (!PasswordEncoderProvider.get().matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return JwtUtil.generateToken(username);
    }

    public boolean validateToken(String token) {
        try {
            String username = JwtUtil.extractUsername(token);
            return username != null && userService.findByUsername(username) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
