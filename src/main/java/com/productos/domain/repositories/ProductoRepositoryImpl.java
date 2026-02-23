package com.productos.domain.repositories;

import com.productos.domain.entities.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import com.shared.infrastructure.TransactionManager;

public class ProductoRepositoryImpl implements ProductoRepository {

    private final EntityManager em;

    public ProductoRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Producto> findByName(String nombre) {
        TypedQuery<Producto> q = em.createQuery(
            "SELECT p FROM Producto p WHERE p.nombre = :nombre", 
            Producto.class
        );
        q.setParameter("nombre", nombre);
        return q.getResultStream().findFirst();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return Optional.ofNullable(em.find(Producto.class, id));
    }

@Override
    public void save(Producto producto) {
        TransactionManager.doInTransaction(em, () -> {
            if (producto.getId() == null) {
                em.persist(producto);
            } else {
                em.merge(producto);
            }
        });
    }

    @Override
    public void delete(Producto producto) {
        TransactionManager.doInTransaction(em, () -> {
            em.remove(em.contains(producto) ? producto : em.merge(producto));
        });
    }

    @Override
    public boolean existsByName(String nombre) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(p) FROM Producto p WHERE p.nombre = :nombre", 
            Long.class
        );
        q.setParameter("nombre", nombre);
        return q.getSingleResult() > 0;
    }

    @Override
    public List<Producto> findAllPaginated(int page, int size) {
        int offset = page * size;
        TypedQuery<Producto> q = em.createQuery(
            "SELECT p FROM Producto p ORDER BY p.id", 
            Producto.class
        );
        q.setFirstResult(offset);
        q.setMaxResults(size);
        return q.getResultList();
    }

    @Override
    public long count() {
        return em.createQuery("SELECT COUNT(p) FROM Producto p", Long.class)
                .getSingleResult();
    }

    @Override
    public List<Producto> findAll() {
        return em.createQuery("SELECT p FROM Producto p ORDER BY p.id", Producto.class)
                .getResultList();
    }

    @Override
    public void update(Producto producto) {
        TransactionManager.doInTransaction(em, () -> {
            em.merge(producto);
        });
    }
}