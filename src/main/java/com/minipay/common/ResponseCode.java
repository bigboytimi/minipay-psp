package com.minipay.common;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

public enum ResponseCode {

    APPROVED("00", "Approved or completed successfully"),

    DO_NOT_HONOR("05", "Do not honor"),

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

}
