package com.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final DateTimeFormatter ISO_FORMATTER = 
        DateTimeFormatter.ISO_INSTANT;

    private String status;
    private String message;
    private T data;
    private String timestamp; 
    private String path;

    private ApiResponse(Builder<T> builder) {
        this.status = builder.status;
        this.message = builder.message;
        this.data = builder.data;
        this.timestamp = ISO_FORMATTER.format(builder.timestamp);
        this.path = builder.path;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }

    public static <T> ApiResponse<T> success(T data) {
        return new Builder<T>()
                .withStatus("success")
                .withData(data)
                .withTimestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new Builder<T>()
                .withStatus("success")
                .withMessage(message)
                .withData(data)
                .withTimestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return new Builder<T>()
                .withStatus("error")
                .withMessage(message)
                .withTimestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String path) {
        return new Builder<T>()
                .withStatus("error")
                .withMessage(message)
                .withPath(path)
                .withTimestamp(Instant.now())
                .build();
    }

    public static class Builder<T> {
        private String status;
        private String message;
        private T data;
        private Instant timestamp = Instant.now();
        private String path;

        public Builder<T> withStatus(String status) { 
            this.status = status; 
            return this; 
        }
        
        public Builder<T> withMessage(String message) { 
            this.message = message; 
            return this; 
        }
        
        public Builder<T> withData(T data) { 
            this.data = data; 
            return this; 
        }
        
        public Builder<T> withTimestamp(Instant timestamp) { 
            this.timestamp = timestamp; 
            return this; 
        }
        
        public Builder<T> withPath(String path) { 
            this.path = path; 
            return this; 
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}