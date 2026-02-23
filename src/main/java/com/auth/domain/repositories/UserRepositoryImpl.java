package com.auth.domain.repositories;

import com.auth.domain.entities.UserEntity;
import com.shared.infrastructure.TransactionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class);
        q.setParameter("username", username);
        return q.getResultStream().findFirst();
    }

    @Override
    public void save(UserEntity user) {
        TransactionManager.doInTransaction(em, () -> {
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
        });
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(em.find(UserEntity.class, id));
    }

    @Override
    public void delete(UserEntity user) {
        TransactionManager.doInTransaction(em, () -> {
            em.remove(em.contains(user) ? user : em.merge(user));
        });
    }

    @Override
    public boolean existsByUsername(String username) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(u) FROM UserEntity u WHERE u.username = :username", Long.class);
        q.setParameter("username", username);
        return q.getSingleResult() > 0;
    }
}