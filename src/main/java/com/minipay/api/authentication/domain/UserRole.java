package com.minipay.api.authentication.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Table(name = "user_role_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role_id", nullable = false, unique = true)
    private String roleId;

}
