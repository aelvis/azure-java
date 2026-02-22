package com.shared.utils;

import java.util.HashMap;
import java.util.Map;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;


public final class ResponseFactory {
    
    private ResponseFactory() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    // ===== RESPUESTAS EXITOSAS =====
    
    public static HttpResponseMessage success(HttpRequestMessage<?> request, Object data) {
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(JsonResponse.success(data))
                .build();
    }
    
    public static HttpResponseMessage success(HttpRequestMessage<?> request, String message) {
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(JsonResponse.success(message))
                .build();
    }
    
    public static HttpResponseMessage created(HttpRequestMessage<?> request, Object data) {
        return request.createResponseBuilder(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(JsonResponse.success(data))
                .build();
    }
    
    public static HttpResponseMessage created(HttpRequestMessage<?> request, String message) {
        return request.createResponseBuilder(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(JsonResponse.success(message))
                .build();
    }
    
    public static HttpResponseMessage noContent(HttpRequestMessage<?> request) {
        return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                .build();
    }

    // ===== RESPUESTAS DE ERROR =====
    
    public static HttpResponseMessage error(HttpRequestMessage<?> request, HttpStatus status, String message) {
        return request.createResponseBuilder(status)
                .header("Content-Type", "application/json")
                .body(JsonResponse.error(message))
                .build();
    }
    
    public static HttpResponseMessage badRequest(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.BAD_REQUEST, message);
    }
    
    public static HttpResponseMessage unauthorized(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.UNAUTHORIZED, message);
    }
    
    public static HttpResponseMessage forbidden(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.FORBIDDEN, message);
    }
    
    public static HttpResponseMessage notFound(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.NOT_FOUND, message);
    }
    
    public static HttpResponseMessage conflict(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.CONFLICT, message);
    }
    
    public static HttpResponseMessage serverError(HttpRequestMessage<?> request, String message) {
        return error(request, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    public static HttpResponseMessage serverError(HttpRequestMessage<?> request, Exception e) {
        return error(request, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    // ===== RESPUESTAS CON DATOS ADICIONALES =====
    
    public static HttpResponseMessage successWithHeaders(
            HttpRequestMessage<?> request, 
            Object data, 
            Map<String, String> headers) {
        
        HttpResponseMessage.Builder builder = request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(JsonResponse.success(data));
        
        headers.forEach(builder::header);
        return builder.build();
    }
    
    public static HttpResponseMessage paginated(
            HttpRequestMessage<?> request,
            Object data,
            int page,
            int size,
            long total) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("page", page);
        response.put("size", size);
        response.put("total", total);
        response.put("totalPages", (int) Math.ceil((double) total / size));
        
        return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .header("X-Page", String.valueOf(page))
                .header("X-Size", String.valueOf(size))
                .header("X-Total", String.valueOf(total))
                .body(JsonResponse.success(response))
                .build();
    }
}