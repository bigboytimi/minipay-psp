package com.minipay.api.payment.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class PaymentResponse {
    private String paymentReference;
    private String orderId;
    private String customerId;
    private String channel;
    private String currency;
    private String status;
    private BigDecimal msc;
    private BigDecimal vatAmount;
    private BigDecimal processorFee;
    private BigDecimal processorVat;
    private BigDecimal payableVat;
    private BigDecimal amountPayable;
}
