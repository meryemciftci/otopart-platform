package com.otopart.domain.order.controller;

import com.otopart.domain.order.dto.CreateOrderRequest;
import com.otopart.domain.order.entity.Order;
import com.otopart.domain.order.service.OrderService;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.shared.enums.OrderStatus;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Siparis olusturma ve takip")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Siparis olustur")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Siparisıniz alindi!", orderService.createOrder(user, request)));
    }

    @GetMapping
    @Operation(summary = "Siparislerim")
    public ResponseEntity<ApiResponse<Page<Order>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getUserOrders(user.getId(), PageRequest.of(page, size))));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Siparis detayi")
    public ResponseEntity<ApiResponse<Order>> getOrder(
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getByOrderNumber(orderNumber)));
    }

    @PutMapping("/{orderNumber}/cancel")
    @Operation(summary = "Siparisi iptal et")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                "Siparis iptal edildi",
                orderService.updateStatus(orderNumber, OrderStatus.CANCELLED)));
    }

    @PutMapping("/admin/{orderNumber}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Siparis durumunu guncelle")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Durum guncellendi",
                orderService.updateStatus(orderNumber, status)));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
    }
}