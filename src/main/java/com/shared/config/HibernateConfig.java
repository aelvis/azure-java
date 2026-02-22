package com.shared.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class HibernateConfig {

    private static final EntityManagerFactory entityManagerFactory;

    private HibernateConfig() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    static {
        validateEnvironmentVariables();
        try {
            // Propiedades que sobreescriben persistence.xml
            Map<String, Object> properties = new HashMap<>();

            // Configuración de conexión desde variables de entorno
            properties.put("jakarta.persistence.jdbc.url", System.getenv("DB_URL"));
            properties.put("jakarta.persistence.jdbc.user", System.getenv("DB_USER"));
            properties.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
            properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

            // Propiedades de Hibernate
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "true");

            // Usar HikariCP como pool
            properties.put("hibernate.connection.provider_class",
                    "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
            properties.put("hibernate.hikari.maximumPoolSize", "10");
            properties.put("hibernate.hikari.minimumIdle", "5");
            properties.put("hibernate.hikari.idleTimeout", "300000"); // 5 minutos
            properties.put("hibernate.hikari.connectionTimeout", "30000"); // 30 segundos
            properties.put("hibernate.hikari.maxLifetime", "1800000"); // 30 minutos
            // Crear EntityManagerFactory usando persistence.xml + propiedades
            entityManagerFactory = Persistence.createEntityManagerFactory("defaultPU", properties);

        } catch (Exception ex) {
            throw new ExceptionInInitializerError("Error en Hibernate: " + ex.getMessage());
        }
    }

    private static void validateEnvironmentVariables() {
        String[] required = { "DB_URL", "DB_USER", "DB_PASSWORD" };
        for (String envVar : required) {
            if (System.getenv(envVar) == null || System.getenv(envVar).trim().isEmpty()) {
                throw new ExceptionInInitializerError("Variable de entorno faltante: " + envVar);
            }
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}