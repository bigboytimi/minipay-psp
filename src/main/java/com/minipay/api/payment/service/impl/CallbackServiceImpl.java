package com.minipay.api.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.service.CallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackServiceImpl implements CallbackService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendWebhook(Payment payment, String url, String webhookSecret) {
        Map<String, Object> payload = Map.ofEntries(
                Map.entry("paymentRef", payment.getPaymentReference()),
                Map.entry("orderId", payment.getOrderId()),
                Map.entry("status", payment.getPaymentStatus().name()),
                Map.entry("amount", payment.getAmount()),
                Map.entry("currency", payment.getCurrency().name()),
                Map.entry("msc", payment.getMsc()),
                Map.entry("vatAmount", payment.getVatAmount()),
                Map.entry("processorFee", payment.getProcessorFee()),
                Map.entry("processorVat", payment.getProcessorVat()),
                Map.entry("amountPayable", payment.getAmountPayable()),
                Map.entry("timestamp", Instant.now().toString())
        );

        try {

            sendWebhook(url, webhookSecret, payload);

        } catch (Exception e) {
            log.warn("Webhook send failed: {}. Retrying up to 3 times...", e.getMessage());

            int maxRetries = 3;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    Thread.sleep(2000L * attempt);
                    sendWebhook(url, webhookSecret, payload);
                    log.info("Webhook retry succeeded on attempt {}", attempt);
                    return;
                } catch (Exception retryEx) {
                    log.error("Retry {} failed: {}", attempt, retryEx.getMessage());
                    if (attempt == maxRetries) {
                        log.error("All retries failed for paymentRef {}", payment.getPaymentReference());
                        //todo: persist failed webhook later
                    }
                }
            }
        }
    }

    private void sendWebhook(String url, String webhookSecret, Map<String, Object> payload) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String canonicalJson = objectMapper.writeValueAsString(payload);

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getEncoder().encodeToString(mac.doFinal(canonicalJson.getBytes(StandardCharsets.UTF_8)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Signature", signature);

        HttpEntity<String> entity = new HttpEntity<>(canonicalJson, headers);

        restTemplate.postForEntity(url, entity, Void.class);
    }
}

