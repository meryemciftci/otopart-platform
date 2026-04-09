package com.otopart.domain.order.repository;

import com.otopart.domain.order.entity.Order;
import com.otopart.shared.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status != 'CANCELLED'")
    long countCompletedOrders(@Param("userId") Long userId);
}