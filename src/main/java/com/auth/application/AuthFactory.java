package com.auth.application;

import com.auth.domain.repositories.UserRepositoryImpl;
import com.auth.domain.repositories.RoleRepositoryImpl;
import jakarta.persistence.EntityManager;

public class AuthFactory {
    
    private AuthFactory() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }
    
    private static UserRepositoryImpl createUserRepository(EntityManager em) {
        return new UserRepositoryImpl(em);
    }
    private static RoleRepositoryImpl createRoleRepository(EntityManager em) {
        return new RoleRepositoryImpl(em);
    }
    
    public static UserService createUserService(EntityManager em) {
        UserRepositoryImpl userRepo = createUserRepository(em);
        RoleRepositoryImpl roleRepo = createRoleRepository(em);
        return new UserService(userRepo, roleRepo);
    }
    
    public static AuthService createAuthService(EntityManager em) {
        UserService userService = createUserService(em);
        return new AuthService(userService);
    }
}