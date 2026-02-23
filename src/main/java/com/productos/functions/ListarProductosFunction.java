package com.productos.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.productos.application.ProductoFactory;
import com.productos.domain.entities.Producto;
import com.productos.domain.repositories.ProductoRepository;
import com.productos.domain.dto.ProductoResponse;
import com.productos.domain.dto.ProductoPaginadoResponse;
import com.shared.dto.ResponseEnvelope;
import com.shared.infrastructure.BaseFunction;
import com.shared.security.AuthMiddleware;
import com.shared.utils.DbContext;
import com.shared.utils.SafeLogger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListarProductosFunction extends BaseFunction<Optional<String>, ResponseEnvelope<ProductoPaginadoResponse>> {

    private static final String ROUTE = "producto/listar";

    @FunctionName("productos_Listar")
    public HttpResponseMessage listar(
            @HttpTrigger(
                name = "req", 
                methods = HttpMethod.GET, 
                authLevel = AuthorizationLevel.ANONYMOUS, 
                route = ROUTE
            ) 
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        return execute(request, context, body -> {
            SafeLogger log = new SafeLogger(context.getLogger());
            
            String username = AuthMiddleware.authenticate(request, "ROLE_ADMIN", "ROLE_USER");
            log.info("Usuario {} está listando productos", username);
            
            int page = getPage(request);
            int size = getSize(request);
            
            try (DbContext db = new DbContext()) {
                ProductoRepository repo = ProductoFactory.createProductoRepository(db.em());
                
                List<Producto> productos = repo.findAllPaginated(page, size);
                long total = repo.count();
                
                List<ProductoResponse> productosDto = productos.stream()
                        .map(ProductoResponse::fromEntity)
                        .collect(Collectors.toList());
                
                ProductoPaginadoResponse paginado = new ProductoPaginadoResponse(
                    productosDto, page, size, total
                );
                
                log.info("Listando {} productos de {} totales (página {})", 
                        productos.size(), total, page);
                
                return ResponseEnvelope.ok(
                    paginado, 
                    String.format("Página %d de productos", page)
                );
            }
        }, AuthRequirement.REQUIRED);
    }
}