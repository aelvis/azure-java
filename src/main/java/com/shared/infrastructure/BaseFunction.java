package com.shared.infrastructure;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.productos.domain.exceptions.ProductoAlreadyExistsException;

import java.util.UUID;
import java.util.logging.Level;

import com.auth.domain.exceptions.InvalidCredentialsException;
import com.auth.domain.exceptions.UserAlreadyExistsException;
import com.auth.domain.exceptions.UserNotFoundException;
import com.microsoft.azure.functions.ExecutionContext;
import com.shared.security.AuthMiddleware;
import com.shared.security.exception.AuthenticationException;
import com.shared.utils.ResponseFactory;
import com.shared.utils.SafeLogger;
import jakarta.validation.ConstraintViolationException;

import com.shared.utils.RequestHelper;

public abstract class BaseFunction<T, R> {

    protected HttpResponseMessage execute(
            HttpRequestMessage<T> request,
            ExecutionContext context,
            FunctionAction<T, R> action,
            AuthRequirement auth) {

        SafeLogger log = new SafeLogger(context.getLogger());
        String requestId = UUID.randomUUID().toString();
        log.info("[{}] Inicio: {} {}",
                requestId,
                request.getHttpMethod(),
                request.getUri().getPath());

        long startTime = System.currentTimeMillis();

        try {
            if (auth == AuthRequirement.REQUIRED) {
                String username = AuthMiddleware.authenticate(request);
                log.fine("[{}] Usuario autenticado: {}", requestId, username);
            }

            R result = action.execute(request.getBody());

            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] Completado en {}ms", requestId, duration);

            return ResponseFactory.success(request, result);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            log.severe("[{}] Error en {}ms: {}", requestId, duration, e.getMessage());
            log.log(Level.FINE, "Stacktrace detallado", e);
            return handleException(request, e);
        }
    }

    private HttpResponseMessage handleException(HttpRequestMessage<T> request, Exception e) {
        if (e instanceof AuthenticationException) {
            return ResponseFactory.unauthorized(request, e.getMessage());
        }
        if (e instanceof UserNotFoundException) {
            return ResponseFactory.notFound(request, e.getMessage());
        }
        if (e instanceof UserAlreadyExistsException || e instanceof ProductoAlreadyExistsException) {
            return ResponseFactory.conflict(request, e.getMessage());
        }
        if (e instanceof InvalidCredentialsException) {
            return ResponseFactory.unauthorized(request, e.getMessage());
        }
        if (e instanceof IllegalArgumentException || e instanceof ConstraintViolationException) {
            return ResponseFactory.badRequest(request, e.getMessage());
        }
        return ResponseFactory.serverError(request, "Error interno del servidor");
    }

    protected int getPage(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "page", 0);
    }

    protected int getSize(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "size", 10);
    }

    @FunctionalInterface
    protected interface FunctionAction<T, R> {
        R execute(T body) throws FunctionExecutionException;
    }

    protected enum AuthRequirement {
        REQUIRED, OPTIONAL
    }
}