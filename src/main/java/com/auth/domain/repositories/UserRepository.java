package com.auth.domain.repositories;

import com.auth.domain.entities.UserEntity;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByUsername(String username);
    void save(UserEntity user);
    Optional<UserEntity> findById(Long id);
    void delete(UserEntity user);
    boolean existsByUsername(String username);
}