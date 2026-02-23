package com.productos.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.productos.domain.dto.RegisterProducto;
import com.productos.domain.dto.ProductoResponse;
import com.productos.domain.entities.Producto;
import com.productos.domain.repositories.ProductoRepository;
import com.productos.domain.exceptions.ProductoAlreadyExistsException;
import com.productos.application.ProductoFactory;
import com.shared.dto.ResponseEnvelope;
import com.shared.infrastructure.BaseFunction;
import com.shared.security.AuthMiddleware;
import com.shared.utils.DbContext;
import com.shared.utils.SafeLogger;
import com.shared.utils.ValidationUtils;

public class RegistrarProductoFunction extends BaseFunction<RegisterProducto, ResponseEnvelope<ProductoResponse>> {

    private static final String ROUTE = "producto/register";

    @FunctionName("productos_Register")
    public HttpResponseMessage register(
            @HttpTrigger(
                name = "req", 
                methods = HttpMethod.POST, 
                authLevel = AuthorizationLevel.ANONYMOUS, 
                route = ROUTE
            ) 
            HttpRequestMessage<RegisterProducto> request,
            final ExecutionContext context) {

        return execute(request, context, body -> {
            SafeLogger log = new SafeLogger(context.getLogger());
            
            String username = AuthMiddleware.authenticate(request, "ROLE_ADMIN");
            log.info("Usuario admin {} está registrando producto", username);
            
            if (body == null) {
                throw new IllegalArgumentException("Request body no puede ser null");
            }
            
            ValidationUtils.validate(body);
            
            if (body.getNombre() == null || body.getNombre().trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }
            if (body.getDescripcion() == null || body.getDescripcion().trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del producto es obligatoria");
            }
            
            String nombre = body.getNombre().trim();
            String descripcion = body.getDescripcion().trim();
            
            try (DbContext db = new DbContext()) {
                ProductoRepository repo = ProductoFactory.createProductoRepository(db.em());
                
                if (repo.existsByName(nombre)) {
                    throw new ProductoAlreadyExistsException(nombre);
                }
                
                Producto producto = new Producto();
                producto.setNombre(nombre);
                producto.setDescripcion(descripcion);
                
                repo.save(producto);
                
                log.info("Producto registrado exitosamente - ID: {}, Nombre: {}, por usuario: {}", 
                        producto.getId(), producto.getNombre(), username);
                
                ProductoResponse response = ProductoResponse.fromEntity(producto);
                
                String resourcePath = "/api/producto/" + producto.getId();
                
                return ResponseEnvelope.created(
                    response,
                    resourcePath,
                    String.format("Producto '%s' registrado exitosamente", producto.getNombre())
                );
            }
        }, AuthRequirement.REQUIRED);
    }
}