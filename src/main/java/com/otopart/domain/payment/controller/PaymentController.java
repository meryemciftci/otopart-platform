package com.otopart.domain.payment.controller;

import com.otopart.domain.payment.entity.Payment;
import com.otopart.domain.payment.entity.Payment.PaymentMethod;
import com.otopart.domain.payment.service.PaymentService;
import com.otopart.domain.order.entity.Order;
import com.otopart.domain.order.service.OrderService;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Odeme islemleri")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "Odeme baslat")
    public ResponseEntity<ApiResponse<Payment>> createPayment(
            @RequestBody PaymentRequest request) {

        Order order = orderService.getByOrderNumber(request.getOrderNumber());
        Payment payment = paymentService.createPayment(
                order,
                request.getMethod(),
                request.getInstallmentCount());

        return ResponseEntity.ok(ApiResponse.success("Odeme baslatildi", payment));
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(summary = "Siparis odemesi")
    public ResponseEntity<ApiResponse<Payment>> getByOrder(
            @PathVariable String orderNumber) {

        Order order = orderService.getByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getByOrderId(order.getId())));
    }

    @GetMapping("/installment-info")
    @Operation(summary = "Taksit bilgisi hesapla")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInstallmentInfo(
            @RequestParam BigDecimal amount,
            @RequestParam int installments) {

        BigDecimal monthly = paymentService.calculateInstallmentAmount(amount, installments);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "totalAmount", amount,
                "installments", installments,
                "monthlyAmount", monthly
        )));
    }

    @GetMapping("/bank-loan-info")
    @Operation(summary = "Banka kredisi bilgisi")
    public ResponseEntity<ApiResponse<String>> getBankLoanInfo(
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getBankLoanInfo(amount)));
    }

    @PutMapping("/admin/{paymentId}/success")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Odemeyi basarili isle")
    public ResponseEntity<ApiResponse<Payment>> markSuccess(
            @PathVariable Long paymentId,
            @RequestParam String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Odeme onaylandi",
                paymentService.markSuccess(paymentId, transactionId)));
    }

    @PutMapping("/admin/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Iade et")
    public ResponseEntity<ApiResponse<Payment>> refund(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Iade yapildi",
                paymentService.refund(paymentId)));
    }

    @Data
    public static class PaymentRequest {
        private String orderNumber;
        private PaymentMethod method;
        private Integer installmentCount;
    }
}