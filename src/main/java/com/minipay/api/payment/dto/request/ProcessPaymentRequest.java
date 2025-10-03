package com.minipay.api.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class ProcessPaymentRequest {
    @NotBlank(message = "payment reference must not be blank")
    private String paymentReference;
    @NotNull(message = "status must not be blank")
    private boolean status;
}
