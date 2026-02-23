package com.shared.infrastructure;

import jakarta.persistence.EntityManager;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionManager {

    private static final Logger LOGGER = Logger.getLogger(TransactionManager.class.getName());

    private TransactionManager() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static <T> T doInTransaction(EntityManager em, Supplier<T> operation) {
        boolean transaccionIniciada = false;
        try {

            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transaccionIniciada = true;
                LOGGER.fine("Transacción iniciada");
            }

            T result = operation.get();

            if (transaccionIniciada) {
                em.getTransaction().commit();
                LOGGER.fine("Transacción commiteada exitosamente");
            }

            return result;

        } catch (Exception e) {
            if (transaccionIniciada && em.getTransaction().isActive()) {
                try {
                    em.getTransaction().rollback();
                    LOGGER.log(Level.WARNING, "Transacción revertida por error: " + e.getMessage());
                } catch (Exception rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Error haciendo rollback: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Error en operación transaccional: " + e.getMessage(), e);
        }
    }

    public static void doInTransaction(EntityManager em, Runnable operation) {
        doInTransaction(em, () -> {
            operation.run();
            return null;
        });
    }

    public static <T> T executeInTransaction(EntityManager em, TransactionalOperation<T> operation) throws Exception {
        return doInTransaction(em, () -> {
            try {
                return operation.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute() throws Exception;
    }
}