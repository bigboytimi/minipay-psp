package com.minipay.api.authentication.domain;

import com.minipay.api.BaseEntity;
import com.minipay.api.authentication.domain.enums.UserStatus;
import com.minipay.api.merchant.domain.Merchant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
@Table(name = "user_tbl")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}
