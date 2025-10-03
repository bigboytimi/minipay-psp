package com.minipay.api.settlement.domains;

import com.minipay.api.BaseEntity;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.settlement.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Table(name = "settlement_batch_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementBatch extends BaseEntity {
    @Column(name = "settlement_ref", nullable = false, unique = true)
    private String settlementReference;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    @Column(name = "processor_fee", nullable = false)
    private BigDecimal processorFee;
    @Column(name = "processor_vat", nullable = false)
    private BigDecimal processorVat;
    @Column(name = "payable_vat", nullable = false)
    private BigDecimal payableVat;
    @Column(name = "amount_payable", nullable = false)
    private BigDecimal amountPayable;
    @Column(name = "income", nullable = false)
    private BigDecimal income;
    @Column(name = "msc", nullable = false)
    private BigDecimal msc;
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    @Column(name = "count", nullable = false)
    private Long count;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;
}
