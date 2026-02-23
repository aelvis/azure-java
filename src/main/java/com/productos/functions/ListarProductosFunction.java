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
import com.shared.infrastructure.BaseFunction;
import com.shared.security.AuthMiddleware;
import com.shared.utils.DbContext;
import com.shared.utils.SafeLogger;
import java.util.List;
import java.util.Optional;

public class ListarProductosFunction extends BaseFunction<Optional<String>, List<Producto>> {

    private static final String ROUTE = "producto/listar";

    @FunctionName("productos_Listar")
    public HttpResponseMessage listar(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        SafeLogger log = new SafeLogger(context.getLogger());
        return execute(request, context, body -> {
            String username = AuthMiddleware.authenticate(request, "ROLE_ADMIN", "ROLE_USER");
            log.info("Usuario admin {} está listando productos", username);
            int page = getPage(request);
            int size = getSize(request);

            try (DbContext db = new DbContext()) {
                ProductoRepository service = ProductoFactory.createProductoRepository(db.em());
                return service.findAllPaginated(page, size);
            }
        }, AuthRequirement.REQUIRED);
    }
}