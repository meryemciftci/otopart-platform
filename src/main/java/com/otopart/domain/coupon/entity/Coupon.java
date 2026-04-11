package com.otopart.domain.coupon.entity;

import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    private Double discountPercent;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    private Integer totalUsageLimit;

    @Builder.Default
    private Integer usedCount = 0;

    @Builder.Default
    private boolean firstOrderOnly = false;

    private Long specificUserId;

    @Builder.Default
    private boolean freeShipping = false;

    public enum CouponType {
        FIXED_AMOUNT,
        PERCENTAGE,
        FREE_SHIPPING,
        FIRST_ORDER
    }
}