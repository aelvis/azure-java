package com.productos.domain.repositories;

import com.productos.domain.entities.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    
    Optional<Producto> findByName(String nombre);
    Optional<Producto> findById(Long id);
    void save(Producto producto);
    void delete(Producto producto);
    boolean existsByName(String nombre);
    List<Producto> findAllPaginated(int page, int size);
    long count();
    List<Producto> findAll();
    void update(Producto producto);
}