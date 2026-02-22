package com.auth.application;

import com.auth.domain.entities.UserEntity;
import com.auth.domain.entities.RoleEntity;
import com.auth.domain.repositories.UserRepository;
import com.shared.security.PasswordEncoderProvider;
import com.auth.domain.repositories.RoleRepository;

import jakarta.persistence.EntityManager;

public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserService(EntityManager em) {
        this.userRepo = new UserRepository(em);
        this.roleRepo = new RoleRepository(em);
    }

    public void register(String username, String password, String email) {
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
