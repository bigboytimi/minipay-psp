package com.minipay.common;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

public enum ResponseCode {

    APPROVED("00", "Approved or completed successfully"),
    REFER_TO_ISSUER("01", "Refer to card issuer"),
    INVALID_MERCHANT("03", "Invalid merchant"),
    DO_NOT_HONOR("05", "Do not honor"),
    INVALID_REQUEST("07", "Invalid request"),
    NOT_FOUND("99", "Not found"),
    DUPLICATE("99", "Duplicated"),
    SYSTEM_MALFUNCTION("96", "System malfunction");

    private final String code;
    private final String description;

    ResponseCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ResponseCode fromCode(String code) {
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.code.equals(code)) {
                return responseCode;
            }
        }
        throw new IllegalArgumentException("Unknown ISO8583 response code: " + code);
    }
}
