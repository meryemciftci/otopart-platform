package com.otopart.domain.delivery.controller;

import com.otopart.domain.delivery.entity.Delivery;
import com.otopart.domain.delivery.entity.Delivery.DeliveryStatus;
import com.otopart.domain.delivery.service.CourierService;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courier")
@RequiredArgsConstructor
@Tag(name = "Courier", description = "Kurye ve kargo islemleri")
public class CourierController {

    private final CourierService courierService;
    private final UserRepository userRepository;

    @GetMapping("/deliveries/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Bekleyen teslimatlar")
    public ResponseEntity<ApiResponse<Page<Delivery>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                courierService.getPendingDeliveries(PageRequest.of(page, size))));
    }

    @PutMapping("/deliveries/{deliveryId}/assign/{courierId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Kuryeye ata")
    public ResponseEntity<ApiResponse<Delivery>> assignCourier(
            @PathVariable Long deliveryId,
            @PathVariable Long courierId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Kurye atandi", courierService.assignCourier(deliveryId, courierId)));
    }

    @PutMapping("/deliveries/{deliveryId}/status")
    @PreAuthorize("hasAnyRole('COURIER', 'ADMIN')")
    @Operation(summary = "Teslimat durumunu guncelle")
    public ResponseEntity<ApiResponse<Delivery>> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                "Durum guncellendi", courierService.updateStatus(deliveryId, status)));
    }

    @PutMapping("/deliveries/{deliveryId}/location")
    @PreAuthorize("hasAnyRole('COURIER', 'ADMIN')")
    @Operation(summary = "Kurye konumunu guncelle")
    public ResponseEntity<ApiResponse<Delivery>> updateLocation(
            @PathVariable Long deliveryId,
            @RequestBody LocationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                courierService.updateLocation(deliveryId, request.getLatitude(), request.getLongitude())));
    }

    @PutMapping("/deliveries/{deliveryId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Kargo numarasi ekle")
    public ResponseEntity<ApiResponse<Delivery>> addTracking(
            @PathVariable Long deliveryId,
            @RequestParam String cargoCompany,
            @RequestParam String trackingNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                courierService.addTrackingNumber(deliveryId, cargoCompany, trackingNumber)));
    }

    @GetMapping("/my-deliveries")
    @PreAuthorize("hasRole('COURIER')")
    @Operation(summary = "Kurye aktif teslimatlarim")
    public ResponseEntity<ApiResponse<List<Delivery>>> getMyDeliveries(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                courierService.getCourierActiveDeliveries(user.getId())));
    }

    @Data
    static class LocationRequest {
        private Double latitude;
        private Double longitude;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
    }
}