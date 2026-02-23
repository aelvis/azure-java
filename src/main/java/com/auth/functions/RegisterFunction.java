package com.auth.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.auth.domain.dto.RegisterRequest;
import com.auth.domain.dto.UserResponse;
import com.auth.application.AuthFactory;
import com.auth.application.UserService;
import com.auth.domain.entities.UserEntity;
import com.shared.dto.ResponseEnvelope;
import com.shared.infrastructure.BaseFunction;
import com.shared.utils.DbContext;
import com.shared.utils.ValidationUtils;

public class RegisterFunction extends BaseFunction<RegisterRequest, ResponseEnvelope<UserResponse>> {

    private static final String ROUTE = "register";

    @FunctionName("auth_register")
    public HttpResponseMessage register(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) 
            HttpRequestMessage<RegisterRequest> request,
            final ExecutionContext context) {

        return execute(request, context, body -> {
            ValidationUtils.validate(body);

            try (DbContext db = new DbContext()) {
                UserService service = AuthFactory.createUserService(db.em());
                
                UserEntity createdUser = service.register(
                    body.getUsername(), 
                    body.getPassword(), 
                    body.getEmail()
                );
                String resourcePath = "/api/user/" + createdUser.getId();
                UserResponse response = new UserResponse(
                    createdUser.getId(),
                    createdUser.getUsername(),
                    createdUser.getEmail()
                );
                return ResponseEnvelope.created(
                    response,
                    resourcePath,
                    "Usuario registrado exitosamente"
                );
            }
        }, AuthRequirement.OPTIONAL);
    }
}