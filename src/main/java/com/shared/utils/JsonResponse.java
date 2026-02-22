package com.shared.utils;

import java.util.HashMap;
import java.util.Map;

public final class JsonResponse {

    private static final String STATUS = "status";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failed";
    private static final String DATA = "data";
    private static final String MESSAGE = "message";

    private JsonResponse() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static Map<String, Object> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, STATUS_SUCCESS);
        response.put(DATA, data);
        return response;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, STATUS_FAILED);
        response.put(MESSAGE, message);
        return response;
    }

    public static Map<String, Object> error(String message, Object additionalData) {
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, STATUS_FAILED);
        response.put(MESSAGE, message);
        if (additionalData != null) {
            response.put(DATA, additionalData);
        }
        return response;
    }
}