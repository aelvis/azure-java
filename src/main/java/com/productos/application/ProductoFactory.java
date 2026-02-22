package com.productos.application;

import com.productos.domain.repositories.ProductoRepository;
import com.productos.domain.repositories.ProductoRepositoryImpl;
import jakarta.persistence.EntityManager;

public class ProductoFactory {
    
    private ProductoFactory() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }
    
    public static ProductoRepository createProductoRepository(EntityManager em) {
        return new ProductoRepositoryImpl(em);
    }
    
    public static ProductoService createProductoService(EntityManager em) {
        ProductoRepository repo = createProductoRepository(em);
        return new ProductoService(repo);
    }
}