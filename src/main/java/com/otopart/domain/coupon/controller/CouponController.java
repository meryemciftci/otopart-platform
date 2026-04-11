package com.otopart.domain.coupon.controller;

import com.otopart.domain.coupon.entity.Coupon;
import com.otopart.domain.coupon.service.CouponService;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Kupon islemleri")
public class CouponController {

    private final CouponService couponService;
    private final UserRepository userRepository;

    @GetMapping("/validate/{code}")
    @Operation(summary = "Kuponu dogrula")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(
            @PathVariable String code,
            @RequestParam BigDecimal orderAmount,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);
        Coupon coupon = couponService.validate(code, user.getId(), orderAmount);
        BigDecimal discount = couponService.calculateDiscount(coupon, orderAmount);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "code", coupon.getCode(),
                "type", coupon.getType(),
                "discountAmount", discount,
                "freeShipping", coupon.isFreeShipping(),
                "description", coupon.getDescription() != null ? coupon.getDescription() : ""
        )));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Kupon olustur")
    public ResponseEntity<ApiResponse<Coupon>> create(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(ApiResponse.success("Kupon olusturuldu", couponService.create(coupon)));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
    }
}