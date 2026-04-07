package com.otopart.domain.supplier.repository;

import com.otopart.domain.supplier.entity.Supplier;
import com.otopart.shared.enums.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByCode(String code);
    List<Supplier> findByWarehouseCityAndActiveTrue(City city);
    List<Supplier> findByAnkaraWarehouseTrueAndActiveTrue();
}