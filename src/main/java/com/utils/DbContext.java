package com.utils;

import com.config.HibernateConfig;

import jakarta.persistence.EntityManager;

public class DbContext implements AutoCloseable {

    private final EntityManager em;

    public DbContext() {
        this.em = HibernateConfig.getEntityManagerFactory().createEntityManager();
    }

    public EntityManager em() {
        return em;
    }

    @Override
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
