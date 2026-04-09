package com.otopart.domain.loyalty.controller;

import com.otopart.domain.loyalty.service.LoyaltyService;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/loyalty")
@RequiredArgsConstructor
@Tag(name = "Loyalty", description = "Sadakat puani islemleri")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    @Operation(summary = "Puan ozeti")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);

        Map<String, Object> summary = Map.of(
                "totalPoints", user.getLoyaltyPoints(),
                "pointsToFreeShipping", loyaltyService.pointsToFreeShipping(user),
                "hasFreeShipping", loyaltyService.hasFreeShipping(user),
                "freeShippingReason", loyaltyService.getFreeShippingReason(user) != null
                        ? loyaltyService.getFreeShippingReason(user)
                        : "No free shipping available",
                "totalOrders", user.getTotalOrderCount()
        );

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
    }
}