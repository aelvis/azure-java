package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.domain.dto.LoginRequest;
import com.domain.dto.RegisterRequest;
import com.domain.dto.JwtResponse;
import com.service.AuthService;
import com.service.UserService;
import com.utils.DbContext;
import com.utils.JsonResponse;

public class AuthFunction {

        @FunctionName("register")
        public HttpResponseMessage register(
                        @HttpTrigger(name = "req", methods = {
                                        HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<RegisterRequest> request,
                        final ExecutionContext context) {
                try {

                        RegisterRequest body = request.getBody();

                        if (body == null || body.getUsername() == null || body.getPassword() == null) {
                                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                .body(JsonResponse.error("username y password son obligatorios"))
                                                .build();
                        }

                        try (DbContext db = new DbContext()) {
                                UserService service = new UserService(db.em());
                                service.register(body.getUsername(), body.getPassword(), body.getEmail());
                        }
                        return request.createResponseBuilder(HttpStatus.OK)
                                        .body(JsonResponse.success("Usuario registrado correctamente"))
                                        .build();

                } catch (Exception ex) {

                        return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                                        .body(JsonResponse.error(ex.getMessage()))
                                        .build();
                }
        }

        @FunctionName("login")
        public HttpResponseMessage login(
                        @HttpTrigger(name = "req", methods = {
                                        HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<LoginRequest> request,
                        final ExecutionContext context) {

                LoginRequest body = request.getBody();
                if (body == null || body.getUsername() == null || body.getPassword() == null) {
                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                        .body("Username y password son requeridos")
                                        .build();
                }

                try (DbContext db = new DbContext()) {
                        AuthService service = new AuthService(db.em());
                        String token = service.login(body.getUsername(), body.getPassword());
                        if (token == null) {
                                return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                                                .body("Credenciales inválidas")
                                                .build();
                        }

                        return request.createResponseBuilder(HttpStatus.OK)
                                        .body(new JwtResponse(token))
                                        .build();
                } catch (Exception ex) {
                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(JsonResponse.error(ex.getMessage()))
                                        .build();
                }

        }
}
