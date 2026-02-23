package com.shared.infrastructure;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.productos.domain.exceptions.ProductoAlreadyExistsException;

import java.util.UUID;
import java.util.logging.Level;
import java.util.function.Function;
import com.auth.domain.exceptions.InvalidCredentialsException;
import com.auth.domain.exceptions.UserAlreadyExistsException;
import com.auth.domain.exceptions.UserNotFoundException;
import com.microsoft.azure.functions.ExecutionContext;
import com.shared.dto.ApiResponse;
import com.shared.dto.ResponseEnvelope;
import com.shared.security.AuthMiddleware;
import com.shared.security.exception.AuthenticationException;
import com.shared.utils.SafeLogger;
import jakarta.validation.ConstraintViolationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shared.utils.RequestHelper;

public abstract class BaseFunction<T, R> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected HttpResponseMessage execute(
            HttpRequestMessage<T> request,
            ExecutionContext context,
            Function<T, R> action,
            AuthRequirement auth) {

        SafeLogger log = new SafeLogger(context.getLogger());
        String requestId = UUID.randomUUID().toString();

        log.info("[{}] Inicio: {} {}", requestId, request.getHttpMethod(), request.getUri().getPath());

        long startTime = System.currentTimeMillis();

        try {
            if (auth == AuthRequirement.REQUIRED) {
                String username = AuthMiddleware.authenticate(request);
                log.fine("[{}] Usuario autenticado: {}", requestId, username);
            }

            R result = action.apply(request.getBody());
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] Completado en {}ms", requestId, duration);

            return buildResponse(request, result);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.severe("[{}] Error en {}ms: {}", requestId, duration, e.getMessage());
            log.log(Level.FINE, "Stacktrace detallado", e);
            return handleException(request, e);
        }
    }

    private HttpResponseMessage buildResponse(HttpRequestMessage<T> request, R result) {
        try {
            if (result instanceof ResponseEnvelope) {
                ResponseEnvelope<?> envelope = (ResponseEnvelope<?>) result;

                switch (envelope.getType()) {
                    case CREATED:
                        return buildCreatedResponse(request, envelope);
                    case NO_CONTENT:
                        return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();
                    case OK:
                    default:
                        return buildSuccessResponse(request, envelope.getData());
                }
            }

            return buildSuccessResponse(request, result);

        } catch (Exception e) {
            return handleException(request, e);
        }
    }

    private HttpResponseMessage buildSuccessResponse(HttpRequestMessage<T> request, Object data) {
        ApiResponse<Object> apiResponse = ApiResponse.success(data);
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(apiResponse)
                .build();
    }

    private HttpResponseMessage buildCreatedResponse(
            HttpRequestMessage<T> request,
            ResponseEnvelope<?> envelope) {

        ApiResponse<Object> apiResponse = ApiResponse.success(
                envelope.getMessage() != null ? envelope.getMessage() : "Recurso creado exitosamente",
                envelope.getData());

        String location = request.getUri().resolve(envelope.getResourcePath()).toString();

        return request.createResponseBuilder(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .header("Location", location)
                .body(apiResponse)
                .build();
    }

    private HttpResponseMessage handleException(HttpRequestMessage<T> request, Exception e) {
        String path = request.getUri().getPath();
        ApiResponse<Object> errorResponse;
        HttpStatus status;
        if (e instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorResponse = ApiResponse.error(e.getMessage(), path);
        } else if (e instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorResponse = ApiResponse.error(e.getMessage(), path);
        } else if (e instanceof UserAlreadyExistsException ||
                e instanceof ProductoAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
            errorResponse = ApiResponse.error(e.getMessage(), path);
        } else if (e instanceof InvalidCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
            errorResponse = ApiResponse.error("Credenciales inválidas", path);
        } else if (e instanceof IllegalArgumentException ||
                e instanceof ConstraintViolationException) {
            status = HttpStatus.BAD_REQUEST;
            errorResponse = ApiResponse.error(e.getMessage(), path);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorResponse = ApiResponse.error("Error interno del servidor", path);
        }

        return request.createResponseBuilder(status)
                .header("Content-Type", "application/json")
                .body(errorResponse)
                .build();
    }

    protected int getPage(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "page", 0);
    }

    protected int getSize(HttpRequestMessage<?> request) {
        return RequestHelper.getIntParam(request, "size", 10);
    }

    protected enum AuthRequirement {
        REQUIRED, OPTIONAL
    }
}