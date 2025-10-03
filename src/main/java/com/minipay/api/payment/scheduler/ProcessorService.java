package com.minipay.api.payment.scheduler;

import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.service.impl.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ProcessorService {
    private final PaymentServiceImpl paymentService;

    @Scheduled(fixedRate = 30000)
    public void processPendingPayments() {
        List<Payment> pendingPayments = paymentService.fetchPendingPayments();
        for (Payment payment : pendingPayments) {
            boolean success = new Random().nextBoolean();
            paymentService.processPayment(payment.getId(), success);
        }
    }
}
