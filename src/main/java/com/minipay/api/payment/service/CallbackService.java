package com.minipay.api.payment.service;


import com.minipay.api.payment.domains.Payment;

public interface CallbackService {
    void sendWebhook(Payment payment, String url, String webhookSecret);
}
