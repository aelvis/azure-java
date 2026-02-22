package com.productos.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.productos.domain.dto.RegisterProducto;
import com.productos.application.ProductoService;
import com.shared.infrastructure.BaseFunction;
import com.shared.utils.DbContext;

public class RegistrarProductoFunction extends BaseFunction {

    private static final String ROUTE = "producto/register";

    @FunctionName("productos_Register")
    public HttpResponseMessage register(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, 
                        authLevel = AuthorizationLevel.ANONYMOUS,
                        route = ROUTE) 
            HttpRequestMessage<RegisterProducto> request,
            final ExecutionContext context) {
        
        return execute(request, context, body -> {
            if (body == null || body.getNombre() == null || body.getDescripcion() == null) {
                throw new IllegalArgumentException("nombre y descripcion son obligatorios");
            }
            
            try (DbContext db = new DbContext()) {
                ProductoService service = new ProductoService(db.em());
                service.register(body.getNombre(), body.getDescripcion());
                return "Producto registrado correctamente";
            }
        }, AuthRequirement.REQUIRED);
    }
}