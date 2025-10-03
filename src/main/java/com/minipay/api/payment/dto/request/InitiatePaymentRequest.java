package com.minipay.api.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class InitiatePaymentRequest {
    @NotBlank(message = "orderId must be provided")
    private String orderId;
    @NotNull(message = "amount must be provided")
    private BigDecimal amount;
    @NotBlank(message = "currency must be provided")
    private String currency;
    @NotBlank(message = "customerId must be provided")
    private String customerId;
    @NotBlank(message = "channel must be provided")
    private String channel;
    @NotBlank(message = "callbackUrl must be provided")
    private String callbackUrl;
}
