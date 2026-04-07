package com.otopart.domain.supplier.service;

import com.otopart.domain.supplier.entity.Supplier;
import com.otopart.domain.supplier.repository.SupplierRepository;
import com.otopart.shared.enums.City;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tedarikçi bulunamadı"));
    }

    public Supplier getByCode(String code) {
        return supplierRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Tedarikçi bulunamadı: " + code));
    }

    public List<Supplier> getAnkaraSuppliers() {
        return supplierRepository.findByAnkaraWarehouseTrueAndActiveTrue();
    }

    public List<Supplier> getByCity(City city) {
        return supplierRepository.findByWarehouseCityAndActiveTrue(city);
    }

    @Transactional
    public Supplier create(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void delete(Long id) {
        Supplier supplier = getById(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
}