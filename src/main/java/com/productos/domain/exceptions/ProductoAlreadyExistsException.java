package com.productos.domain.exceptions;

public class ProductoAlreadyExistsException extends RuntimeException {
    private final String nombre;
    
    public ProductoAlreadyExistsException(String nombre) {
        super("El producto ya existe: " + nombre);
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }
}