package com.usuarios.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.shared.infrastructure.BaseFunction;
import com.shared.security.AuthMiddleware;
import java.util.Map;
import java.util.Optional;

public class ObtenerPerfilFunction extends BaseFunction {

    private static final String ROUTE = "me";

    @FunctionName("obtenerPerfil")
    public HttpResponseMessage me(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        return execute(request, context, body -> {
            String username = AuthMiddleware.authenticate(request);
            return Map.of("username", username);
        }, AuthRequirement.REQUIRED);
    }
}