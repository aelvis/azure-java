package com.productos.application;

import com.productos.domain.entities.Producto;
import com.productos.domain.repositories.ProductoRepository;
import com.productos.domain.exceptions.ProductoAlreadyExistsException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoService {
    
    private final ProductoRepository productoRepo; 

    public ProductoService(ProductoRepository productoRepo) {
        this.productoRepo = productoRepo;
    }

    public void register(String nombre, String descripcion) {
        if (productoRepo.existsByName(nombre)) {
            throw new ProductoAlreadyExistsException(nombre);
        }

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
        response.put("totalPages", (int) Math.ceil((double) total / size));
        response.put("data", productos);
        
        return response;
    }
    
    public Producto findByName(String nombre) {
        return productoRepo.findByName(nombre)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + nombre));
    }
    
    public boolean existsByName(String nombre) {
        return productoRepo.existsByName(nombre);
    }
}