package com.minipay.exception;

public class ApiException extends RuntimeException {
    private String code;

    public ApiException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ApiException(String message) {
        super(message);
    }

    public String getCode() {
        return code;
    }

}
