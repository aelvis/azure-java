package com.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.config.HibernateConfig;
import com.domain.dto.LoginRequest;
import com.domain.dto.RegisterRequest;
import com.domain.dto.JwtResponse;
import com.service.AuthService;
import com.service.UserService;

import jakarta.persistence.EntityManager;

public class AuthFunction {

    @FunctionName("register")
    public HttpResponseMessage register(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<RegisterRequest> request,
            final ExecutionContext context) {

        EntityManager em = null;

        try {
            em = HibernateConfig.getEntityManagerFactory().createEntityManager();
            UserService service = new UserService(em);

            RegisterRequest body = request.getBody();

            if (body == null || body.username == null || body.password == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Content-Type", "application/json")
                        .body("""
                                {
                                "status": "failed",
                                "message": "username y password son obligatorios"
                                }
                                """)
                        .build();
            }

            service.register(body.username, body.password, body.email);

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body("""
                            {
                            "status": "success",
                            "message": "Usuario registrado correctamente"
                            }
                            """)
                    .build();

        } catch (Exception ex) {

            context.getLogger().severe("Error en register: " + ex.getMessage());

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body("""
                            {
                            "status": "failed",
                            "message": "%s"
                            }
                            """.formatted(ex.getMessage()))
                    .build();

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @FunctionName("login")
    public HttpResponseMessage login(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<LoginRequest> request,
            final ExecutionContext context) {

        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        AuthService service = new AuthService(em);

        LoginRequest body = request.getBody();
        String token = service.login(body.username, body.password);

        if (token == null) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas")
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .body(new JwtResponse(token))
                .build();
    }
}
