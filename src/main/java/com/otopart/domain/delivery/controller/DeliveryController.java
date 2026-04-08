package com.otopart.domain.delivery.controller;

import com.otopart.domain.delivery.service.DeliverySlotService;
import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.service.ProductService;
import com.otopart.shared.enums.City;
import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "Teslimat slot ve bilgi endpoint'leri")
public class DeliveryController {

    private final DeliverySlotService deliverySlotService;
    private final ProductService productService;

    @GetMapping("/slots")
    @Operation(summary = "Uygun teslimat slotlarini getir")
    public ResponseEntity<ApiResponse<List<String>>> getSlots(
            @RequestParam(required = false) String date) {

        LocalDate deliveryDate = date != null
                ? LocalDate.parse(date)
                : LocalDate.now();

        List<String> slots = deliverySlotService.getAvailableSlots(deliveryDate)
                .stream()
                .map(LocalTime::toString)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(slots));
    }

    @GetMapping("/info/{productId}")
    @Operation(summary = "Urune gore teslimat bilgisi ve etiketi")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDeliveryInfo(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "ANKARA") String city) {

        Product product = productService.getById(productId);
        City customerCity = City.valueOf(city.toUpperCase());

        DeliveryType deliveryType = deliverySlotService.resolveDeliveryType(product, customerCity);
        LocalDate deliveryDate = deliverySlotService.calculateDeliveryDate(deliveryType);
        String label = deliverySlotService.getDeliveryLabel(deliveryType);
        boolean sameDayAvailable = deliverySlotService.isSameDayAvailable();

        Map<String, Object> info = Map.of(
                "deliveryType", deliveryType,
                "deliveryDate", deliveryDate.toString(),
                "label", label,
                "sameDayAvailable", sameDayAvailable,
                "cutoffTime", deliverySlotService.getCutoffTime().toString(),
                "availableSlots", deliverySlotService.getAvailableSlots(deliveryDate)
                        .stream().map(LocalTime::toString).toList()
        );

        return ResponseEntity.ok(ApiResponse.success(info));
    }
}