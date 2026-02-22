package com.auth.domain.repositories;

import com.auth.domain.entities.RoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepository {

    private final EntityManager em;

    public RoleRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<RoleEntity> findByName(String name) {
        TypedQuery<RoleEntity> q = em.createQuery(
            "SELECT r FROM RoleEntity r WHERE r.name = :name", RoleEntity.class);
        q.setParameter("name", name);
        return q.getResultStream().findFirst();
    }

    @Override
    public Optional<RoleEntity> findById(Long id) {
        return Optional.ofNullable(em.find(RoleEntity.class, id));
    }

    @Override
    public List<RoleEntity> findAll() {
        return em.createQuery("SELECT r FROM RoleEntity r", RoleEntity.class)
                .getResultList();
    }

    @Override
    public void save(RoleEntity role) {
        em.getTransaction().begin();
        if (role.getId() == null) {
            em.persist(role);
        } else {
            em.merge(role);
        }
        em.getTransaction().commit();
    }
}