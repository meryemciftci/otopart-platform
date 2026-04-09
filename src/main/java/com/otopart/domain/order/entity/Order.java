package com.otopart.domain.order.entity;

import com.otopart.domain.user.entity.User;
import com.otopart.domain.vehicle.entity.Vehicle;
import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.enums.OrderStatus;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    private boolean b2bDiscountApplied = false;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal b2bDiscountAmount = BigDecimal.ZERO;

    @Builder.Default
    private boolean freeShipping = false;

    private String freeShippingReason;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    private LocalDate deliveryDate;
    private LocalTime deliverySlot;

    private String deliveryAddress;
    private String deliveryCity;

    private String couponCode;

    @Builder.Default
    private Integer pointsEarned = 0;

    private String paymentMethod;
    private Integer installmentCount;

    @Column(columnDefinition = "TEXT")
    private String customerNote;
}