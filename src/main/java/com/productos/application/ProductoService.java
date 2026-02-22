package com.productos.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.productos.domain.entities.Producto;
import com.productos.domain.repositories.ProductoRepository;

import jakarta.persistence.EntityManager;

public class ProductoService {
    private final ProductoRepository productoRepo;

    public ProductoService(EntityManager em) {
        this.productoRepo = new ProductoRepository(em);
    }

    public void register(String nombre, String descripcion) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        productoRepo.save(producto);
    }

    public Map<String, Object> listarPaginado(int page, int size) {
        List<Producto> productos = productoRepo.findAllPaginated(page, size);
        long total = productoRepo.count();
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("size", size);
        response.put("total", total);
        response.put("data", productos);
        return response;
    }
}
