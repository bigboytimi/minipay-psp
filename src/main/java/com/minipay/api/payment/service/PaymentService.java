package com.minipay.api.payment.service;

import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.dto.request.ProcessPaymentRequest;
import com.minipay.api.payment.dto.request.InitiatePaymentRequest;
import com.minipay.api.payment.dto.response.PaymentResponse;
import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.domains.SettlementItem;
import com.minipay.common.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

public interface PaymentService {
    PaymentResponse payment(InitiatePaymentRequest request);
    PaymentResponse approvePayment(String paymentRef, boolean success);

    Payment save(Payment payment);

    PaymentResponse fetch(String paymentRef);

    List<PaymentResponse> filter(LocalDateTime startDate, LocalDateTime endDate, String merchantId, String channel, String status);

    PaymentResponse processPayment(ProcessPaymentRequest request);

    List<Payment> getUnsettledPayments(PaymentStatus status, LocalDateTime from, LocalDateTime to);

    List<Payment> getPayments(LocalDateTime startDate, LocalDateTime endDate, PaymentChannel channel, String merchantId, PaymentStatus status);
}
