package com.otopart.domain.delivery.entity;

import com.otopart.domain.order.entity.Order;
import com.otopart.domain.user.entity.User;
import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private User courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryType deliveryType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.PENDING;

    private LocalDate scheduledDate;
    private LocalTime scheduledSlot;

    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    // Kargo entegrasyonu
    private String cargoCompany;
    private String trackingNumber;

    // Kurye konum takibi
    private Double courierLatitude;
    private Double courierLongitude;
    private LocalDateTime locationUpdatedAt;

    private String address;
    private String recipientName;
    private String recipientPhone;

    @Builder.Default
    private boolean requiresLargeVehicle = false;

    public enum DeliveryStatus {
        PENDING,
        ASSIGNED,
        PICKED_UP,
        ON_THE_WAY,
        DELIVERED,
        FAILED,
        RETURNED
    }
}