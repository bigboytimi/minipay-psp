package com.minipay.api.settlement.domains;

import com.minipay.api.BaseEntity;
import com.minipay.api.payment.domains.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Table(name = "settlement_item_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private SettlementBatch batch;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "msc", nullable = false)
    private BigDecimal msc;
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;
    @Column(name = "processor_fee", nullable = false)
    private BigDecimal processorFee;
    @Column(name = "processor_vat", nullable = false)
    private BigDecimal processorVat;
    @Column(name = "amount_payable", nullable = false)
    private BigDecimal amountPayable;
}
