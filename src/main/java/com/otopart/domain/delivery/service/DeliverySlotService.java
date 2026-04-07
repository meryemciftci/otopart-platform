package com.otopart.domain.delivery.service;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.supplier.entity.Supplier;
import com.otopart.shared.enums.City;
import com.otopart.shared.enums.DeliveryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class DeliverySlotService {

    @Value("${delivery.slots}")
    private String deliverySlotsRaw;

    @Value("${delivery.ankara-cutoff-hour}")
    private int ankaraCutoffHour;

    @Value("${delivery.next-day-cities}")
    private String nextDayCitiesRaw;

    @Value("${delivery.cargo-days}")
    private int cargoDays;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private List<String> getDeliverySlots() {
        return Arrays.asList(deliverySlotsRaw.split(","));
    }

    private List<String> getNextDayCities() {
        return Arrays.asList(nextDayCitiesRaw.split(","));
    }

    public DeliveryType resolveDeliveryType(Product product, City customerCity) {
        if (product.isExpressAvailable()) return DeliveryType.EXPRESS;
        if (product.isRequiresLargeVehicle()) return DeliveryType.LARGE_VEHICLE;

        Supplier supplier = product.getSupplier();
        if (supplier == null) return DeliveryType.STANDARD;

        if (Boolean.TRUE.equals(supplier.getAnkaraWarehouse())
                || supplier.getWarehouseCity() == City.ANKARA) {
            return DeliveryType.SAME_DAY_SLOT;
        }

        if (getNextDayCities().contains(supplier.getWarehouseCity().name())) {
            return DeliveryType.NEXT_DAY;
        }

        return DeliveryType.CARGO_2_DAY;
    }

    public boolean isSameDayAvailable() {
        return LocalTime.now().isBefore(LocalTime.of(ankaraCutoffHour, 0));
    }

    public LocalTime getCutoffTime() {
        return LocalTime.of(ankaraCutoffHour, 0);
    }

    public LocalDate calculateDeliveryDate(DeliveryType type) {
        LocalDate today = LocalDate.now();
        return switch (type) {
            case SAME_DAY_SLOT, EXPRESS -> isSameDayAvailable() ? today : today.plusDays(1);
            case NEXT_DAY, LARGE_VEHICLE -> today.plusDays(1);
            case CARGO_2_DAY -> today.plusDays(cargoDays);
            default -> today.plusDays(3);
        };
    }

    public List<LocalTime> getAvailableSlots(LocalDate date) {
        List<String> slots = getDeliverySlots();
        if (date.isAfter(LocalDate.now())) {
            return slots.stream()
                    .map(s -> LocalTime.parse(s, TIME_FORMAT))
                    .toList();
        }
        LocalTime now = LocalTime.now();
        return slots.stream()
                .map(s -> LocalTime.parse(s, TIME_FORMAT))
                .filter(slot -> now.plusHours(1).isBefore(slot))
                .toList();
    }

    public String getDeliveryLabel(DeliveryType type) {
        return switch (type) {
            case SAME_DAY_SLOT -> isSameDayAvailable()
                    ? "Saat " + getCutoffTime() + "'a kadar ver, bugün gelsin! 🚚"
                    : "Yarın teslimat - 4 farklı saat seçeneği";
            case NEXT_DAY -> "Yarın teslimat 📦";
            case CARGO_2_DAY -> "2 iş günü içinde kargo";
            case EXPRESS -> "Hemen Gelsin! 🚀";
            case LARGE_VEHICLE -> "Büyük araçla teslimat";
            default -> "Standart kargo";
        };
    }
}