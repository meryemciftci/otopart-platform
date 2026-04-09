package com.otopart.domain.loyalty.service;

import com.otopart.domain.order.repository.OrderRepository;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Value("${loyalty.points-per-order-tl}")
    private int pointsPerTl;

    @Value("${loyalty.free-shipping-threshold}")
    private int freeShippingThreshold;

    @Value("${loyalty.first-orders-free-shipping}")
    private int firstOrdersFreeShipping;

    /** Siparis tamamlandiginda puan ekle */
    @Transactional
    public int addPoints(User user, BigDecimal orderAmount) {
        int points = orderAmount.intValue() * pointsPerTl;
        user.setLoyaltyPoints(user.getLoyaltyPoints() + points);
        userRepository.save(user);
        log.info("User {} earned {} points. Total: {}", user.getId(), points, user.getLoyaltyPoints());
        return points;
    }

    /** Puan kullanarak kargo ucretsiz mi? */
    public boolean isFreeShippingByPoints(User user) {
        return user.getLoyaltyPoints() >= freeShippingThreshold;
    }

    /** Ilk 3 siparis ucretsiz mi? */
    public boolean isFreeShippingByOrderCount(User user) {
        long completed = orderRepository.countCompletedOrders(user.getId());
        return completed < firstOrdersFreeShipping;
    }

    /** Herhangi bir ucretsiz kargo hakki var mi? */
    public boolean hasFreeShipping(User user) {
        return isFreeShippingByPoints(user) || isFreeShippingByOrderCount(user);
    }

    /** Ucretsiz kargo gerekçesi */
    public String getFreeShippingReason(User user) {
        if (isFreeShippingByOrderCount(user)) {
            long completed = orderRepository.countCompletedOrders(user.getId());
            return "First " + firstOrdersFreeShipping + " orders free shipping (" + (completed + 1) + ". order)";
        }
        if (isFreeShippingByPoints(user)) {
            return freeShippingThreshold + " points used for free shipping";
        }
        return null;
    }

    /** Bir sonraki ucretsiz kargoya kac puan kaldi? */
    public int pointsToFreeShipping(User user) {
        return Math.max(0, freeShippingThreshold - user.getLoyaltyPoints());
    }

    /** Puan kullan */
    @Transactional
    public void usePointsForShipping(User user) {
        if (isFreeShippingByPoints(user)) {
            user.setLoyaltyPoints(user.getLoyaltyPoints() - freeShippingThreshold);
            userRepository.save(user);
        }
    }
}