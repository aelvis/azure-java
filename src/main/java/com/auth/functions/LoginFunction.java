package com.auth.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.auth.domain.dto.LoginRequest;
import com.auth.domain.exceptions.InvalidCredentialsException;
import com.auth.domain.exceptions.UserNotFoundException;
import com.auth.domain.dto.JwtResponse;
import com.auth.application.AuthFactory;
import com.auth.application.AuthService;
import com.shared.dto.ResponseEnvelope;
import com.shared.infrastructure.BaseFunction;
import com.shared.utils.DbContext;
import com.shared.utils.ValidationUtils;
import com.shared.utils.SafeLogger;

public class LoginFunction extends BaseFunction<LoginRequest, ResponseEnvelope<JwtResponse>> {

    private static final String ROUTE = "login";

    @FunctionName("auth_login")
    public HttpResponseMessage login(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = ROUTE) HttpRequestMessage<LoginRequest> request,
            final ExecutionContext context) {

        return execute(request, context, body -> {
            SafeLogger log = new SafeLogger(context.getLogger());
            ValidationUtils.validate(body);
            checkRateLimit(request.getUri().getHost(), log);
            return performLogin(body, log);
        }, AuthRequirement.OPTIONAL);
    }

    private void checkRateLimit(String clientIp, SafeLogger log) {
        log.fine("Login attempt from IP: {}", clientIp);
    }

    private ResponseEnvelope<JwtResponse> performLogin(LoginRequest body, SafeLogger log) {
        long startTime = System.currentTimeMillis();

        try (DbContext db = new DbContext()) {
            AuthService service = AuthFactory.createAuthService(db.em());

            String token = service.login(body.getUsername(), body.getPassword());
            long duration = System.currentTimeMillis() - startTime;

            log.info("Login exitoso - Usuario: {}, Tiempo: {}ms",
                    body.getUsername(), duration);
            JwtResponse jwtResponse = new JwtResponse(token);
            return ResponseEnvelope.ok(jwtResponse, "Login exitoso");

        } catch (UserNotFoundException e) {
            log.warning("Login fallido - Usuario no encontrado: {}", body.getUsername());
            throw new IllegalArgumentException("Credenciales inválidas");

        } catch (InvalidCredentialsException e) {
            log.warning("Login fallido - Password incorrecto para: {}", body.getUsername());
            throw new IllegalArgumentException("Credenciales inválidas");

        } catch (Exception e) {
            log.severe("Error inesperado en login para usuario: {} - Error: {}",
                    body.getUsername(), e.getMessage());
            throw e;
        }
    }
}