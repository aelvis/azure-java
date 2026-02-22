package com.utils;

import java.util.HashMap;
import java.util.Map;

public class JsonResponse {

    public static Map<String, Object> success(Object data) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("data", data);
        return res;
    }

    public static Map<String, Object> error(String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "failed");
        res.put("message", message);
        return res;
    }
}
