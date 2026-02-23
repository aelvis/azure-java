package com.auth.application;

import com.auth.domain.entities.UserEntity;
import com.auth.domain.exceptions.UserAlreadyExistsException;

import java.util.List;

import com.auth.domain.entities.RoleEntity;
import com.auth.domain.repositories.UserRepository;
import com.auth.domain.repositories.RoleRepository;
import com.shared.infrastructure.TransactionManager;
import com.shared.security.PasswordEncoderProvider;

import jakarta.persistence.EntityManager;

public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EntityManager em;

    public UserService(UserRepository userRepo, RoleRepository roleRepo, EntityManager em) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.em = em;
    }

    public void register(String username, String password, String email) {
        TransactionManager.doInTransaction(em, () -> {
            if (userRepo.existsByUsername(username)) {
                throw new UserAlreadyExistsException(username);
            }
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(PasswordEncoderProvider.get().encode(password));
            user.setEmail(email);

            RoleEntity role = roleRepo.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER no existe"));

            user.getRoles().add(role);
            userRepo.save(user);
            return null;
        });
    }

    public void updateUserWithRoles(Long userId, String newUsername, List<String> roleNames) {
        TransactionManager.doInTransaction(em, () -> {
            UserEntity user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            user.setUsername(newUsername);
            userRepo.save(user);

            user.getRoles().clear();

            for (String roleName : roleNames) {
                RoleEntity role = roleRepo.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                user.getRoles().add(role);
            }
            userRepo.save(user);

            return null;
        });
    }

    public UserEntity findByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }
}