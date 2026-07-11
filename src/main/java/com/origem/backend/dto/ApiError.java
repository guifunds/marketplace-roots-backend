package com.origem.backend.dto;

import java.util.List;

public record ApiError(String message, List<FieldError> fieldErrors) {

    public record FieldError(String field, String message) {
    }

    public static ApiError of(String message) {
        return new ApiError(message, List.of());
    }

    public static ApiError of(String message, List<FieldError> fieldErrors) {
        return new ApiError(message, fieldErrors);
    }
}
