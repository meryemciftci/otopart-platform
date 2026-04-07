package com.otopart.domain.supplier.controller;

import com.otopart.domain.supplier.entity.Supplier;
import com.otopart.domain.supplier.service.SupplierService;
import com.otopart.shared.enums.City;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Tedarikçi işlemleri")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Tüm tedarikçiler")
    public ResponseEntity<ApiResponse<List<Supplier>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Tedarikçi detayı")
    public ResponseEntity<ApiResponse<Supplier>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getById(id)));
    }

    @GetMapping("/ankara")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Ankara deposu olan tedarikçiler")
    public ResponseEntity<ApiResponse<List<Supplier>>> getAnkaraSuppliers() {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getAnkaraSuppliers()));
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Şehre göre tedarikçiler")
    public ResponseEntity<ApiResponse<List<Supplier>>> getByCity(@PathVariable City city) {
        return ResponseEntity.ok(ApiResponse.success(supplierService.getByCity(city)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Tedarikçi ekle")
    public ResponseEntity<ApiResponse<Supplier>> create(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(ApiResponse.success("Tedarikçi eklendi", supplierService.create(supplier)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Tedarikçi sil")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Tedarikçi silindi")
                .build());
    }
}