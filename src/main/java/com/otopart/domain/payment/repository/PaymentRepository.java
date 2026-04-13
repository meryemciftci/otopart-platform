package com.otopart.domain.payment.repository;

import com.otopart.domain.payment.entity.Payment;
import com.otopart.domain.payment.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    long countByStatus(PaymentStatus status);
}