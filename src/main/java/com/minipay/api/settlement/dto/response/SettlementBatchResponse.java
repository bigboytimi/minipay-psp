package com.minipay.api.settlement.dto.response;

import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SettlementBatchResponse {
    private String settlementReference;
    private Merchant merchant;
    private BigDecimal processorFee;
    private BigDecimal processorVat;
    private BigDecimal payableVat;
    private BigDecimal amountPayable;
    private BigDecimal income;
    private BigDecimal msc;
    private BigDecimal vatAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long count;
    private BigDecimal amount;
    private SettlementStatus status;
}
