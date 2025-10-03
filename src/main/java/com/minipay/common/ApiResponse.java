package com.minipay.common;

import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class ApiResponse<T> {
    private String status;
    private String message;
    private String responseCode;
    private T data;
    private List<String> errors;
    private long timestamp;
    private String traceId;


    public ApiResponse(String status, String message, String responseCode, T data) {
        this.status = status;
        this.message = message;
        this.responseCode = responseCode;
        this.data = data;
        this.errors = null;
        this.timestamp = System.currentTimeMillis();
        this.traceId = generateTraceId();
    }


    public ApiResponse(String status, String message, String responseCode, List<String> errors) {
        this.status = status;
        this.message = message;
        this.responseCode = responseCode;
        this.errors = errors;
        this.data = null;
        this.timestamp = System.currentTimeMillis();
        this.traceId = generateTraceId();
    }


    private String generateTraceId() {
        return java.util.UUID.randomUUID().toString();
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(Constants.SUCCESS, message, ResponseCode.APPROVED.getCode(), data);
        return ResponseEntity.ok(response);
    }


    public static ApiResponse<Object> error(List<String> errors, String message, String code) {
        return new ApiResponse<>(Constants.ERROR, message, code, errors);
    }

    public static ResponseEntity<ApiResponse<Object>> error(String error, String message, String code, int httpCode) {
        ApiResponse<Object> response = new ApiResponse<>(Constants.ERROR, message, code, List.of(error));
        return ResponseEntity.status(httpCode).body(response);
    }


}
