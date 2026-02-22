package com.auth.application;

import com.auth.domain.entities.UserEntity;
import com.auth.domain.exceptions.UserAlreadyExistsException;
import com.auth.domain.entities.RoleEntity;
import com.auth.domain.repositories.UserRepository;
import com.auth.domain.repositories.RoleRepository;
import com.shared.security.PasswordEncoderProvider;

public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public void register(String username, String password, String email) {
        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsException(username);
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(PasswordEncoderProvider.get().encode(password));
        user.setEmail(email);

        RoleEntity role = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER no existe"));

        user.getRoles().add(role);
        userRepo.save(user);
    }

    public UserEntity findByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }
}