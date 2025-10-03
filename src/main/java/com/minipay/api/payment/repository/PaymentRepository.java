package com.minipay.api.payment.repository;

import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByPaymentReference(String paymentRef);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    boolean existsByPaymentReference(String paymentReference);

    @Query("SELECT p FROM Payment p " +
            "WHERE (:startDate IS NULL OR p.createdAt >= :startDate) " +
            "AND   (:endDate IS NULL OR p.createdAt <= :endDate) " +
            "AND   (:merchant IS NULL OR p.merchant = :merchant) " +
            "AND   (:channel IS NULL OR p.paymentChannel = :channel) " +
            "AND   (:status IS NULL OR p.paymentStatus = :status)")
    List<Payment> filterPayments(LocalDateTime startDate, LocalDateTime endDate, Merchant merchant, PaymentChannel channel, PaymentStatus status);

    List<Payment> findByPaymentStatusAndSettledFalseAndCreatedAtBetween(
            PaymentStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    Optional<Payment> findByOrderId(String orderId);

}
