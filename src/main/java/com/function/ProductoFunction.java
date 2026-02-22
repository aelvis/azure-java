package com.function;

import java.util.Map;
import java.util.Optional;

import com.domain.dto.RegisterProducto;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.security.AuthMiddleware;
import com.service.ProductoService;
import com.utils.DbContext;
import com.utils.JsonResponse;

public class ProductoFunction {

    @FunctionName("productoRegister")
    public HttpResponseMessage register(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "producto/register") HttpRequestMessage<RegisterProducto> request,
            final ExecutionContext context) {

        try {
            AuthMiddleware.authenticate(request);

            RegisterProducto body = request.getBody();
            if (body == null || body.getNombre() == null || body.getDescripcion() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(JsonResponse.error("nombre y descripcion son obligatorios"))
                        .build();
            }

            try (DbContext db = new DbContext()) {
                ProductoService service = new ProductoService(db.em());
                service.register(body.getNombre(), body.getDescripcion());
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(JsonResponse.success("Producto registrado correctamente"))
                    .build();

        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(JsonResponse.error(ex.getMessage()))
                    .build();
        }
    }

    @FunctionName("listarProductos")
    public HttpResponseMessage listar(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS, route = "producto/listar") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
            AuthMiddleware.authenticate(request);

            int page = Integer.parseInt(request.getQueryParameters().getOrDefault("page", "0"));
            int size = Integer.parseInt(request.getQueryParameters().getOrDefault("size", "10"));

            Map<String, Object> result;
            try (DbContext db = new DbContext()) {
                ProductoService service = new ProductoService(db.em());
                result = service.listarPaginado(page, size);
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(JsonResponse.success(result))
                    .build();

        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(JsonResponse.error(ex.getMessage()))
                    .build();
        }
    }

}
