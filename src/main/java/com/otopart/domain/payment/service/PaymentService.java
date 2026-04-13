package com.otopart.domain.payment.service;

import com.otopart.domain.order.entity.Order;
import com.otopart.domain.order.repository.OrderRepository;
import com.otopart.domain.payment.entity.Payment;
import com.otopart.domain.payment.entity.Payment.PaymentMethod;
import com.otopart.domain.payment.entity.Payment.PaymentStatus;
import com.otopart.domain.payment.repository.PaymentRepository;
import com.otopart.shared.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Payment createPayment(Order order, PaymentMethod method, Integer installmentCount) {
        Payment payment = Payment.builder()
                .order(order)
                .method(method)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .installmentCount(installmentCount)
                .referenceCode(generateReferenceCode())
                .build();
        return paymentRepository.save(payment);
    }

    /** Odeme basarili */
    @Transactional
    public Payment markSuccess(Long paymentId, String transactionId) {
        Payment payment = getById(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        // Siparis durumunu guncelle
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        log.info("Payment {} successful for order {}", paymentId, order.getOrderNumber());
        return paymentRepository.save(payment);
    }

    /** Odeme basarisiz */
    @Transactional
    public Payment markFailed(Long paymentId, String reason) {
        Payment payment = getById(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.warn("Payment {} failed for order {}: {}", paymentId, order.getOrderNumber(), reason);
        return paymentRepository.save(payment);
    }

    /** Iade */
    @Transactional
    public Payment refund(Long paymentId) {
        Payment payment = getById(paymentId);
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }

    public Payment getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment getByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    /** Taksit tutarini hesapla */
    public BigDecimal calculateInstallmentAmount(BigDecimal total, int installments) {
        return total.divide(BigDecimal.valueOf(installments), 2, java.math.RoundingMode.HALF_UP);
    }

    /** Banka kredisi icin yonlendirme bilgisi */
    public String getBankLoanInfo(BigDecimal amount) {
        return "Kredi tutari: " + amount + " TL. " +
                "Anlasmali bankalarimiz: Ziraat, Garanti, Is Bankasi, Yapi Kredi. " +
                "Bankanizla iletisime gececek, kredi tutari hesabiniza aktarilacaktir.";
    }

    private String generateReferenceCode() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}