package com.otopart.domain.coupon.service;

import com.otopart.domain.coupon.entity.Coupon;
import com.otopart.domain.coupon.repository.CouponRepository;
import com.otopart.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

    public Coupon validate(String code, Long userId, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon is not active");
        }
        if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon has expired");
        }
        if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Coupon is not valid yet");
        }
        if (coupon.getTotalUsageLimit() != null && coupon.getUsedCount() >= coupon.getTotalUsageLimit()) {
            throw new RuntimeException("Coupon usage limit reached");
        }
        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Minimum order amount: " + coupon.getMinOrderAmount() + " TL");
        }
        if (coupon.isFirstOrderOnly()) {
            long orderCount = orderRepository.countCompletedOrders(userId);
            if (orderCount > 0) {
                throw new RuntimeException("This coupon is valid for first order only");
            }
        }
        if (coupon.getSpecificUserId() != null && !coupon.getSpecificUserId().equals(userId)) {
            throw new RuntimeException("This coupon is not valid for your account");
        }

        return coupon;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        return switch (coupon.getType()) {
            case FIXED_AMOUNT -> coupon.getDiscountAmount().min(orderAmount);
            case PERCENTAGE -> {
                BigDecimal discount = orderAmount
                        .multiply(BigDecimal.valueOf(coupon.getDiscountPercent() / 100))
                        .setScale(2, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscountAmount() != null) {
                    discount = discount.min(coupon.getMaxDiscountAmount());
                }
                yield discount;
            }
            case FREE_SHIPPING, FIRST_ORDER -> BigDecimal.ZERO;
        };
    }

    @Transactional
    public void markAsUsed(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);
    }

    @Transactional
    public Coupon create(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists");
        }
        return couponRepository.save(coupon);
    }
}