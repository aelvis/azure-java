package com.shared.utils;

import com.microsoft.azure.functions.HttpRequestMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RequestHelper {
    
    private RequestHelper() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    // ===== PARÁMETROS DE QUERY =====
    
    public static int getIntParam(HttpRequestMessage<?> request, String param, int defaultValue) {
        String value = request.getQueryParameters().get(param);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static long getLongParam(HttpRequestMessage<?> request, String param, long defaultValue) {
        String value = request.getQueryParameters().get(param);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static double getDoubleParam(HttpRequestMessage<?> request, String param, double defaultValue) {
        String value = request.getQueryParameters().get(param);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean getBooleanParam(HttpRequestMessage<?> request, String param, boolean defaultValue) {
        String value = request.getQueryParameters().get(param);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
    
    public static String getStringParam(HttpRequestMessage<?> request, String param, String defaultValue) {
        return request.getQueryParameters().getOrDefault(param, defaultValue);
    }
    
    public static Optional<String> getParam(HttpRequestMessage<?> request, String param) {
        return Optional.ofNullable(request.getQueryParameters().get(param));
    }

    // ===== HEADERS =====
    
    public static String getHeader(HttpRequestMessage<?> request, String header, String defaultValue) {
        String value = request.getHeaders().get(header);
        return value != null ? value : defaultValue;
    }
    
    public static Optional<String> getHeader(HttpRequestMessage<?> request, String header) {
        return Optional.ofNullable(request.getHeaders().get(header));
    }
    
    public static boolean hasHeader(HttpRequestMessage<?> request, String header) {
        return request.getHeaders().containsKey(header);
    }

    // ===== VALIDACIÓN DE BODY =====
    
    public static <T> T validateBody(T body, String... requiredFields) {
        if (body == null) {
            throw new IllegalArgumentException("Request body no puede ser null");
        }
        return body;
    }
    
    public static <T> T validateBody(T body) {
        if (body == null) {
            throw new IllegalArgumentException("Request body requerido");
        }
        return body;
    }
    
    // ===== PAGINACIÓN =====
    
    public static int getPage(HttpRequestMessage<?> request) {
        return getIntParam(request, "page", 0);
    }
    
    public static int getSize(HttpRequestMessage<?> request) {
        return getIntParam(request, "size", 10);
    }
    
    public static int getSize(HttpRequestMessage<?> request, int maxSize) {
        int size = getIntParam(request, "size", 10);
        return Math.min(size, maxSize);
    }
    
    public static String getSortBy(HttpRequestMessage<?> request) {
        return getStringParam(request, "sortBy", "id");
    }
    
    public static String getSortDirection(HttpRequestMessage<?> request) {
        String direction = getStringParam(request, "direction", "asc");
        return direction.equalsIgnoreCase("desc") ? "desc" : "asc";
    }

    // ===== FILTROS =====
    
    public static Map<String, String> getFilters(HttpRequestMessage<?> request, String... filterParams) {
        Map<String, String> filters = new HashMap<>();
        for (String param : filterParams) {
            String value = request.getQueryParameters().get(param);
            if (value != null && !value.trim().isEmpty()) {
                filters.put(param, value.trim());
            }
        }
        return filters;
    }
    
    public static Map<String, Object> getFiltersAsObject(HttpRequestMessage<?> request, String... filterParams) {
        Map<String, Object> filters = new HashMap<>();
        for (String param : filterParams) {
            String value = request.getQueryParameters().get(param);
            if (value != null && !value.trim().isEmpty()) {
                try {
                    filters.put(param, Integer.parseInt(value));
                    continue;
                } catch (NumberFormatException e) {
                    // No es entero
                }
                
                try {
                    filters.put(param, Double.parseDouble(value));
                    continue;
                } catch (NumberFormatException e) {
                    // No es double
                }
                
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    filters.put(param, Boolean.parseBoolean(value));
                } else {
                    filters.put(param, value);
                }
            }
        }
        return filters;
    }
}