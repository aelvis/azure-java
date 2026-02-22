package com.auth.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.auth.domain.dto.RegisterRequest;
import com.auth.application.UserService;
import com.shared.infrastructure.BaseFunction;
import com.shared.utils.DbContext;

public class RegisterFunction extends BaseFunction {

    private static final String ROUTE = "register";

    @FunctionName("auth_register")
    public HttpResponseMessage register(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, 
                        authLevel = AuthorizationLevel.ANONYMOUS,
                        route = ROUTE) 
            HttpRequestMessage<RegisterRequest> request,
            final ExecutionContext context) {
        
        return execute(request, context, body -> {
            if (body == null || body.getUsername() == null || body.getPassword() == null) {
                throw new IllegalArgumentException("username y password son obligatorios");
            }
            
            try (DbContext db = new DbContext()) {
                UserService service = new UserService(db.em());
                service.register(body.getUsername(), body.getPassword(), body.getEmail());
                return "Usuario registrado correctamente";
            }
        }, AuthRequirement.OPTIONAL);
    }
}