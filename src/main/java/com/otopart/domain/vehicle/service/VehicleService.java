package com.otopart.domain.vehicle.service;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.repository.ProductRepository;
import com.otopart.domain.user.entity.User;
import com.otopart.domain.vehicle.dto.AddVehicleRequest;
import com.otopart.domain.vehicle.entity.Vehicle;
import com.otopart.domain.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ProductRepository productRepository;

    public List<Vehicle> getGarage(Long userId) {
        return vehicleRepository.findByUserIdAndActiveTrue(userId);
    }

    @Transactional
    public Vehicle addVehicle(User user, AddVehicleRequest request) {
        if (vehicleRepository.existsByUserIdAndChassisNumber(user.getId(), request.getChassisNumber())) {
            throw new RuntimeException("Bu sase numarasi zaten garajinizda kayitli");
        }

        boolean isDefault = vehicleRepository.findByUserIdAndActiveTrue(user.getId()).isEmpty();

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .chassisNumber(request.getChassisNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .engineCode(request.getEngineCode())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .tireSizeFront(request.getTireSizeFront())
                .tireSizeRear(request.getTireSizeRear())
                .nickname(request.getNickname())
                .isDefault(isDefault)
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void removeVehicle(Long vehicleId, Long userId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Arac bulunamadi"));
        if (!vehicle.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu islem icin yetkiniz yok");
        }
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void setDefault(Long vehicleId, Long userId) {
        List<Vehicle> vehicles = vehicleRepository.findByUserIdAndActiveTrue(userId);
        vehicles.forEach(v -> {
            v.setDefault(v.getId().equals(vehicleId));
            vehicleRepository.save(v);
        });
    }

    public Page<Product> getCompatibleProducts(Long vehicleId, Long userId, Pageable pageable) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Arac bulunamadi"));
        if (!vehicle.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu islem icin yetkiniz yok");
        }
        return productRepository.findCompatibleProducts(
                vehicle.getBrand(), vehicle.getModel(), vehicle.getYear(), pageable);
    }

    public Vehicle getById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Arac bulunamadi"));
    }
}