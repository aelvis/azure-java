package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.security.AuthMiddleware;
import com.utils.JsonResponse;
import com.security.exception.AuthenticationException;
import java.util.Map;
import java.util.Optional;

public class UserInfoFunction {

    @FunctionName("me")
    public HttpResponseMessage me(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String username = AuthMiddleware.authenticate(request);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(JsonResponse.success(Map.of("username", username)))
                    .build();

        } catch (AuthenticationException e) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(JsonResponse.error(e.getMessage()))
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error en /me: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonResponse.error("Error interno del servidor"))
                    .build();
        }
    }
}
