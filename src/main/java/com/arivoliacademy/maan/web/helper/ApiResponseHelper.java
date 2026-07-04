package com.arivoliacademy.maan.web.helper;

import com.arivoliacademy.maan.core.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Optional helpers for building standard API responses in controllers.
 */
public final class ApiResponseHelper {

    private ApiResponseHelper() {
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Created", data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.noContent().build();
    }
}
