package com.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.domain.entities.Producto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ProductoRepository {

    private final EntityManager em;

    public ProductoRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<Producto> findByName(String nombre) {
        TypedQuery<Producto> q = em.createQuery("SELECT u FROM Producto u WHERE u.nombre = :nombre", Producto.class);
        q.setParameter("nombre", nombre);
        return q.getResultStream().findFirst();
    }

    public void save(Producto producto) {
        em.getTransaction().begin();
        em.persist(producto);
        em.getTransaction().commit();
    }

    public List<Producto> findAllPaginated(int page, int size) {
        int offset = page * size;

        TypedQuery<Producto> q = em.createQuery(
                "SELECT p FROM Producto p ORDER BY p.id",
                Producto.class);

        q.setFirstResult(offset); // desde dónde empieza
        q.setMaxResults(size); // cuántos registros devuelve

        return q.getResultList();
    }

    public long count() { 
        return em.createQuery("SELECT COUNT(p) FROM Producto p", Long.class) .getSingleResult(); 
    }

}
