package com.shared.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Map;
import java.util.HashMap;

public class HibernateConfig {

    private HibernateConfig() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {

        if (emf == null) {
            Map<String, Object> props = new HashMap<>();

            props.put("jakarta.persistence.jdbc.url", System.getenv("DB_URL"));
            props.put("jakarta.persistence.jdbc.user", System.getenv("DB_USER"));
            props.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
            props.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

            emf = Persistence.createEntityManagerFactory("defaultPU", props);
        }

        return emf;
    }
}
