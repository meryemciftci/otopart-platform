package com.otopart.domain.payment.entity;

import com.otopart.domain.order.entity.Order;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    private Integer installmentCount;

    private String cardLastFour;
    private String bankName;

    private String transactionId;
    private String referenceCode;

    private LocalDateTime paidAt;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    public enum PaymentMethod {
        CREDIT_CARD,
        DBS,
        BANK_TRANSFER,
        BANK_LOAN
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}