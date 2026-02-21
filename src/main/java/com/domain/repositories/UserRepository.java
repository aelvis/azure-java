package com.domain.repositories;

import com.domain.entities.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class UserRepository {

    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<UserEntity> findByUsername(String username) {
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class);
        q.setParameter("username", username);
        return q.getResultStream().findFirst();
    }

    public void save(UserEntity user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }
}
