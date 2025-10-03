package com.minipay.api.reporting.service;

import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.settlement.enums.SettlementStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface ReportGenerationService {

    ResponseEntity<?> getSettlementReport(String format, String batchReference, String merchantId, SettlementStatus status) throws Exception;

    ResponseEntity<?> getTransactionReport(String format, LocalDateTime startDate, LocalDateTime endDate, PaymentChannel channel, String merchantId, PaymentStatus status) throws Exception;
}
