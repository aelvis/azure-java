package com.auth.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.auth.domain.dto.LoginRequest;
import com.auth.domain.dto.JwtResponse;
import com.auth.application.AuthService;
import com.shared.infrastructure.BaseFunction;
import com.shared.utils.DbContext;

public class LoginFunction extends BaseFunction {

    private static final String ROUTE = "login";

    @FunctionName("auth_login")
    public HttpResponseMessage login(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<LoginRequest> request,
            final ExecutionContext context) {

        return execute(request, context, body -> {
            if (body == null || body.getUsername() == null || body.getPassword() == null) {
                throw new IllegalArgumentException("Username y password son requeridos");
            }

            try (DbContext db = new DbContext()) {
                AuthService service = new AuthService(db.em());
                String token = service.login(body.getUsername(), body.getPassword());

                if (token == null) {
                    throw new IllegalArgumentException("Credenciales inválidas");
                }

                context.getLogger().info("Login exitoso: " + body.getUsername());
                return new JwtResponse(token);
            }
        }, AuthRequirement.OPTIONAL);
    }
}