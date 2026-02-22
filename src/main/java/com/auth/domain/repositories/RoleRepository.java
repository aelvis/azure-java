package com.auth.domain.repositories;

import com.auth.domain.entities.RoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class RoleRepository {

    private final EntityManager em;

    public RoleRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<RoleEntity> findByName(String name) {
        TypedQuery<RoleEntity> q = em.createQuery(
            "SELECT r FROM RoleEntity r WHERE r.name = :name", RoleEntity.class);
        q.setParameter("name", name);
        return q.getResultStream().findFirst();
    }
}
