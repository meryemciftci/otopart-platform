package com.otopart.domain.vehicle.repository;

import com.otopart.domain.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUserIdAndActiveTrue(Long userId);
    Optional<Vehicle> findByUserIdAndIsDefaultTrue(Long userId);
    Optional<Vehicle> findByChassisNumber(String chassisNumber);
    boolean existsByUserIdAndChassisNumber(Long userId, String chassisNumber);
}