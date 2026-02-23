package com.shared.dto;

public class ResponseEnvelope<T> {

    private final T data;
    private final ResponseType type;
    private final String resourcePath;
    private final String message;
    private final Integer statusCode;

    private ResponseEnvelope(Builder<T> builder) {
        this.data = builder.data;
        this.type = builder.type;
        this.resourcePath = builder.resourcePath;
        this.message = builder.message;
        this.statusCode = builder.statusCode;
    }

    public T getData() {
        return data;
    }

    public ResponseType getType() {
        return type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public static class Builder<T> {
        private T data;
        private ResponseType type = ResponseType.OK;
        private String resourcePath;
        private String message;
        private Integer statusCode;

        public Builder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> withType(ResponseType type) {
            this.type = type;
            return this;
        }

        public Builder<T> withResourcePath(String resourcePath) {
            this.resourcePath = resourcePath;
            return this;
        }

        public Builder<T> withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> withStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ResponseEnvelope<T> build() {
            if (type == ResponseType.CREATED && (resourcePath == null || resourcePath.isEmpty())) {
                throw new IllegalArgumentException("CREATED responses must have a resourcePath");
            }
            return new ResponseEnvelope<>(this);
        }
    }

    public static <T> ResponseEnvelope<T> ok(T data) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.OK)
                .build();
    }

    public static <T> ResponseEnvelope<T> ok(T data, String message) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.OK)
                .withMessage(message)
                .build();
    }

    public static <T> ResponseEnvelope<T> created(T data, String resourcePath) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.CREATED)
                .withResourcePath(resourcePath)
                .withMessage("Recurso creado exitosamente")
                .build();
    }

    public static <T> ResponseEnvelope<T> created(T data, String resourcePath, String message) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.CREATED)
                .withResourcePath(resourcePath)
                .withMessage(message)
                .build();
    }

    public static <T> ResponseEnvelope<T> noContent() {
        return new Builder<T>()
                .withType(ResponseType.NO_CONTENT)
                .build();
    }

    public static <T> ResponseEnvelope<T> noContent(String message) {
        return new Builder<T>()
                .withType(ResponseType.NO_CONTENT)
                .withMessage(message)
                .build();
    }

    public static <T> ResponseEnvelope<T> accepted(T data, String message) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.ACCEPTED)
                .withMessage(message)
                .build();
    }

    public static <T> ResponseEnvelope<T> accepted(T data, String resourcePath, String message) {
        return new Builder<T>()
                .withData(data)
                .withType(ResponseType.ACCEPTED)
                .withResourcePath(resourcePath)
                .withMessage(message)
                .build();
    }

    public boolean isCreated() {
        return type == ResponseType.CREATED;
    }

    public boolean isNoContent() {
        return type == ResponseType.NO_CONTENT;
    }

    public boolean isOk() {
        return type == ResponseType.OK;
    }

    public boolean isAccepted() {
        return type == ResponseType.ACCEPTED;
    }

    @Override
    public String toString() {
        return String.format(
                "ResponseEnvelope{type=%s, hasData=%s, resourcePath='%s', message='%s'}",
                type, data != null, resourcePath, message);
    }
}