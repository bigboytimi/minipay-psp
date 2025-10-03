package com.minipay.api.settlement.dto.response;

import com.minipay.api.payment.domains.Payment;
import com.minipay.api.settlement.domains.SettlementBatch;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettlementItemResponse {
    private SettlementBatch batch;
    private Payment payment;
    private BigDecimal amount;
    private BigDecimal msc;
    private BigDecimal vatAmount;
    private BigDecimal processorFee;
    private BigDecimal processorVat;
    private BigDecimal amountPayable;
}
