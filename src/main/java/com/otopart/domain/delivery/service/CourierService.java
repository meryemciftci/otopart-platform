package com.otopart.domain.delivery.service;

import com.otopart.domain.delivery.entity.Delivery;
import com.otopart.domain.delivery.entity.Delivery.DeliveryStatus;
import com.otopart.domain.delivery.repository.DeliveryRepository;
import com.otopart.domain.order.entity.Order;
import com.otopart.domain.order.repository.OrderRepository;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.shared.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /** Siparise teslimat olustur */
    @Transactional
    public Delivery createDelivery(Order order) {
        Delivery delivery = Delivery.builder()
                .order(order)
                .deliveryType(order.getDeliveryType())
                .status(DeliveryStatus.PENDING)
                .scheduledDate(order.getDeliveryDate())
                .scheduledSlot(order.getDeliverySlot())
                .address(order.getDeliveryAddress())
                .requiresLargeVehicle(order.getDeliveryType() != null &&
                        order.getDeliveryType().name().equals("LARGE_VEHICLE"))
                .build();
        return deliveryRepository.save(delivery);
    }

    /** Kuryeye teslimat ata */
    @Transactional
    public Delivery assignCourier(Long deliveryId, Long courierId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        log.info("Delivery {} assigned to courier {}", deliveryId, courierId);
        return deliveryRepository.save(delivery);
    }

    /** Kurye konumunu guncelle */
    @Transactional
    public Delivery updateLocation(Long deliveryId, Double latitude, Double longitude) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setCourierLatitude(latitude);
        delivery.setCourierLongitude(longitude);
        delivery.setLocationUpdatedAt(LocalDateTime.now());
        return deliveryRepository.save(delivery);
    }

    /** Teslimat durumunu guncelle */
    @Transactional
    public Delivery updateStatus(Long deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(newStatus);

        if (newStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
            // Siparis durumunu da guncelle
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }

        return deliveryRepository.save(delivery);
    }

    /** Kargo numarasi ekle */
    @Transactional
    public Delivery addTrackingNumber(Long deliveryId, String cargoCompany, String trackingNumber) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setCargoCompany(cargoCompany);
        delivery.setTrackingNumber(trackingNumber);
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getCourierActiveDeliveries(Long courierId) {
        return deliveryRepository.findByCourierIdAndStatus(courierId, DeliveryStatus.ON_THE_WAY);
    }

    public Page<Delivery> getPendingDeliveries(Pageable pageable) {
        return deliveryRepository.findByStatus(DeliveryStatus.PENDING, pageable);
    }

    public Delivery getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
    }
}