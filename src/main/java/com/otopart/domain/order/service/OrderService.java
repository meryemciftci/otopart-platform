package com.otopart.domain.order.service;

import com.otopart.domain.delivery.service.DeliverySlotService;
import com.otopart.domain.order.dto.CreateOrderRequest;
import com.otopart.domain.order.dto.OrderItemRequest;
import com.otopart.domain.order.entity.Order;
import com.otopart.domain.order.entity.OrderItem;
import com.otopart.domain.order.repository.OrderRepository;
import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.repository.ProductRepository;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.vehicle.entity.Vehicle;
import com.otopart.domain.vehicle.repository.VehicleRepository;
import com.otopart.shared.enums.City;
import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.enums.OrderStatus;
import com.otopart.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final VehicleRepository vehicleRepository;
    private final DeliverySlotService deliverySlotService;

    @Value("${b2b.discount-rate}")
    private double b2bDiscountRate;

    @Value("${loyalty.first-orders-free-shipping}")
    private int firstOrdersFreeShipping;

    @Transactional
    public Order createOrder(User user, CreateOrderRequest request) {

        // Araç seçimi
        Vehicle vehicle = null;
        if (request.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Arac bulunamadi"));
        }

        // Sipariş kalemleri
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        DeliveryType deliveryType = DeliveryType.STANDARD;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Urun bulunamadi: " + itemReq.getProductId()));

            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("Yetersiz stok: " + product.getName());
            }

            BigDecimal unitPrice = product.getDiscountedPrice() != null
                    ? product.getDiscountedPrice()
                    : product.getPrice();

            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            // İlk ürünün teslimat tipini al
            if (deliveryType == DeliveryType.STANDARD) {
                deliveryType = deliverySlotService.resolveDeliveryType(product,
                        user.getCity() != null ? user.getCity() : City.ANKARA);
            }

            // Stok düş
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);

            items.add(OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build());
        }

        // B2B iskonto
        BigDecimal b2bDiscount = BigDecimal.ZERO;
        boolean b2bApplied = false;
        if (user.getRole() == UserRole.MECHANIC) {
            b2bDiscount = subtotal.multiply(BigDecimal.valueOf(b2bDiscountRate))
                    .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.subtract(b2bDiscount);
            b2bApplied = true;
        }

        // Kargo ücreti
        BigDecimal shippingFee = calculateShippingFee(deliveryType);
        boolean freeShipping = false;
        String freeShippingReason = null;

        long completedOrders = orderRepository.countCompletedOrders(user.getId());
        if (completedOrders < firstOrdersFreeShipping) {
            freeShipping = true;
            freeShippingReason = "İlk " + firstOrdersFreeShipping + " siparis ucretsiz kargo";
            shippingFee = BigDecimal.ZERO;
        }

        // Toplam
        BigDecimal totalAmount = subtotal.add(shippingFee);

        // Teslimat tarihi ve slot
        LocalDate deliveryDate = deliverySlotService.calculateDeliveryDate(deliveryType);
        LocalTime deliverySlot = request.getDeliverySlot() != null
                ? LocalTime.parse(request.getDeliverySlot(), DateTimeFormatter.ofPattern("HH:mm"))
                : deliverySlotService.getAvailableSlots(deliveryDate).stream().findFirst().orElse(null);

        // Sipariş oluştur
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .vehicle(vehicle)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(totalAmount)
                .b2bDiscountApplied(b2bApplied)
                .b2bDiscountAmount(b2bDiscount)
                .freeShipping(freeShipping)
                .freeShippingReason(freeShippingReason)
                .deliveryType(deliveryType)
                .deliveryDate(deliveryDate)
                .deliverySlot(deliverySlot)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryCity(request.getDeliveryCity())
                .couponCode(request.getCouponCode())
                .paymentMethod(request.getPaymentMethod())
                .installmentCount(request.getInstallmentCount())
                .customerNote(request.getCustomerNote())
                .pointsEarned(totalAmount.intValue())
                .build();

        order = orderRepository.save(order);

        // Item'lara order bağla
        final Order savedOrder = order;
        items.forEach(item -> item.setOrder(savedOrder));
        order.setItems(items);
        orderRepository.save(order);

        log.info("Order created: {} | User: {} | Total: {} TL",
                order.getOrderNumber(), user.getId(), totalAmount);

        return order;
    }

    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Order getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Siparis bulunamadi: " + orderNumber));
    }

    @Transactional
    public Order updateStatus(String orderNumber, OrderStatus status) {
        Order order = getByOrderNumber(orderNumber);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private BigDecimal calculateShippingFee(DeliveryType type) {
        return switch (type) {
            case SAME_DAY_SLOT, EXPRESS -> BigDecimal.valueOf(19.90);
            case NEXT_DAY -> BigDecimal.valueOf(24.90);
            case LARGE_VEHICLE -> BigDecimal.valueOf(59.90);
            default -> BigDecimal.valueOf(29.90);
        };
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String unique = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "OTO-" + date + "-" + unique;
    }
}