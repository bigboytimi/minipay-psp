package com.minipay.api.payment.enums;

import com.minipay.exception.ApiException;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
public enum PaymentStatus {
    PENDING, SUCCESS, FAILED;

    public static PaymentStatus fromString(String value) throws ApiException {
        if (value == null) {
            throw new ApiException("Payment status cannot be null");
        }
        try {
            return PaymentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid payment status: " + value);
        }
    }
}
