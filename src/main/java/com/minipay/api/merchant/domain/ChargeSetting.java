package com.minipay.api.merchant.domain;

import com.minipay.api.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
@Table(name = "charge_setting_tbl")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChargeSetting extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    @Column(name = "percentage_fee", nullable = false)
    private BigDecimal percentageFee;
    @Column(name = "fixed_fee", nullable = false)
    private BigDecimal fixedFee;
    @Column(name = "use_fixed_msc", nullable = false)
    private Boolean useFixedMSC;
    @Column(name = "msc_cap", nullable = false)
    private BigDecimal mscCap;
    @Column(name = "vat_rate", nullable = false)
    private BigDecimal vatRate;
    @Column(name = "platform_provider_rate", nullable = false)
    private BigDecimal platformProviderRate;
    @Column(name = "platform_provider_cap", nullable = false)
    private BigDecimal platformProviderCap;
}
