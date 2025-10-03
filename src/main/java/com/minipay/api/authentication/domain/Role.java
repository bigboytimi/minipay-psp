package com.minipay.api.authentication.domain;

import com.minipay.api.BaseEntity;
import com.minipay.api.authentication.domain.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Table(name = "role_tbl")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;
}
