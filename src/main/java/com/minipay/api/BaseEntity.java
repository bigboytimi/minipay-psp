package com.minipay.api;

import com.minipay.utils.RandomGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {
    @Id
    private String id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = generateRandomId();
        }
    }

    private String generateRandomId() {
        return RandomGenerator.generateRandomNumber(10);
    }

}
