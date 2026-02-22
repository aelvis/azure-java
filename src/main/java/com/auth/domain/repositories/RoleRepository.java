package com.auth.domain.repositories;

import com.auth.domain.entities.RoleEntity;
import java.util.Optional;
import java.util.List;

public interface RoleRepository {
    Optional<RoleEntity> findByName(String name);
    Optional<RoleEntity> findById(Long id);
    List<RoleEntity> findAll();
    void save(RoleEntity role);
}