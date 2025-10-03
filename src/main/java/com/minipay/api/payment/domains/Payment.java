package com.minipay.api.payment.domains;

import com.minipay.api.BaseEntity;
import com.minipay.api.payment.enums.Currency;
import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.merchant.domain.Merchant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Table(name = "payment_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "payment_ref", nullable = false, unique = true)
    private String paymentReference;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "payment_channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentChannel paymentChannel;
    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Column(name = "msc", nullable = false)
    private BigDecimal msc;
    @Column(name = "vat_amount", nullable = false)
    private BigDecimal vatAmount;

    @Column(name = "processor_fee", nullable = false)
    private BigDecimal processorFee;

    @Column(name = "processor_vat", nullable = false)
    private BigDecimal processorVat;

    @Column(name = "payable_vat", nullable = false)
    private BigDecimal payableVat;
    @Column(name = "amount_payable", nullable = false)
    private BigDecimal amountPayable;
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "settled", nullable = false)
    private boolean settled = false;
    @Column(name = "settled_at", nullable = false)
    private LocalDateTime settledAt;
}
