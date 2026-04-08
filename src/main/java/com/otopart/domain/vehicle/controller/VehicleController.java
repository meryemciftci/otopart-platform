package com.otopart.domain.vehicle.controller;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.user.repository.UserRepository;
import com.otopart.domain.vehicle.dto.AddVehicleRequest;
import com.otopart.domain.vehicle.entity.Vehicle;
import com.otopart.domain.vehicle.service.VehicleService;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Garage", description = "Arac garaji ve sase islemleri")
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserRepository userRepository;

    @GetMapping("/garage")
    @Operation(summary = "Garajdaki araclari listele")
    public ResponseEntity<ApiResponse<List<Vehicle>>> getGarage(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(vehicleService.getGarage(user.getId())));
    }

    @PostMapping("/garage")
    @Operation(summary = "Garaja arac ekle")
    public ResponseEntity<ApiResponse<Vehicle>> addVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddVehicleRequest request) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                "Arac garajiniza eklendi!",
                vehicleService.addVehicle(user, request)));
    }

    @DeleteMapping("/garage/{vehicleId}")
    @Operation(summary = "Garajdan arac sil")
    public ResponseEntity<ApiResponse<Void>> removeVehicle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long vehicleId) {
        User user = getUser(userDetails);
        vehicleService.removeVehicle(vehicleId, user.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Arac garajdan silindi")
                .build());
    }

    @PutMapping("/garage/{vehicleId}/default")
    @Operation(summary = "Varsayilan araci degistir")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long vehicleId) {
        User user = getUser(userDetails);
        vehicleService.setDefault(vehicleId, user.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Varsayilan arac guncellendi")
                .build());
    }

    @GetMapping("/garage/{vehicleId}/compatible-products")
    @Operation(summary = "Araca uygun urunleri listele (garaj etiketi)")
    public ResponseEntity<ApiResponse<Page<Product>>> getCompatibleProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(
                vehicleService.getCompatibleProducts(vehicleId, user.getId(),
                        PageRequest.of(page, size))));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi"));
    }
}