package com.minipay.api.merchant.domain;

import com.minipay.api.BaseEntity;
import com.minipay.api.authentication.domain.User;
import com.minipay.api.authentication.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

@Table(name = "merchant_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merchant extends BaseEntity {
    @Column(name = "merchant_id", nullable = false, unique = true)
    private String merchantId;
    @Column(name = "merchant_name", nullable = false)
    private String name;
    @Column(name = "merchant_email", nullable = false, unique = true)
    private String email;
    @Column(name = "merchant_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "settlement_account", nullable = false)
    private String settlementAccount;

    @Column(name = "settlement_bank", nullable = false)
    private String settlementBank;

    @Column(name = "callback_url", nullable = false)
    private String callBackUrl;

    @Column(name = "webhook_secret", nullable = false)
    private String webhookSecret;

    @OneToMany(mappedBy = "merchant")
    private List<User> users;
}
