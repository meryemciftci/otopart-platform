package com.otopart.domain.delivery.repository;

import com.otopart.domain.delivery.entity.Delivery;
import com.otopart.domain.delivery.entity.Delivery.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByCourierIdAndStatus(Long courierId, DeliveryStatus status);
    Page<Delivery> findByStatus(DeliveryStatus status, Pageable pageable);
    Page<Delivery> findByCourierId(Long courierId, Pageable pageable);
}