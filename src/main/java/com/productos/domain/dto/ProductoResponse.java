package com.productos.domain.dto;

public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;

    public ProductoResponse() {}

    public ProductoResponse(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static ProductoResponse fromEntity(com.productos.domain.entities.Producto entity) {
        return new ProductoResponse(
            entity.getId(),
            entity.getNombre(),
            entity.getDescripcion()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}