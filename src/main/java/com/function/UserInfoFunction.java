package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.security.JwtFilter;
import java.util.Optional;

public class UserInfoFunction {

    @FunctionName("me")
    public HttpResponseMessage me(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        try {
            String authHeader = request.getHeaders().get("Authorization");
            if (authHeader == null) {
                authHeader = request.getHeaders().get("authorization");
            }
            if (authHeader == null || authHeader.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                        .header("Content-Type", "application/json")
                        .body("""
                            {
                              "status": "failed",
                              "message": "Falta el header Authorization"
                            }
                            """)
                        .build();
            }

            String username = JwtFilter.validate(authHeader);

            if (username == null) {
                return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                        .header("Content-Type", "application/json")
                        .body("""
                            {
                              "status": "failed",
                              "message": "Token inválido o expirado"
                            }
                            """)
                        .build();
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body("""
                        {
                          "status": "success",
                          "username": "%s"
                        }
                        """.formatted(username))
                    .build();

        } catch (Exception ex) {
            context.getLogger().severe("Error en /me: " + ex.getMessage());

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body("""
                        {
                          "status": "failed",
                          "message": "%s"
                        }
                        """.formatted(ex.getMessage()))
                    .build();
        }
    }
}
