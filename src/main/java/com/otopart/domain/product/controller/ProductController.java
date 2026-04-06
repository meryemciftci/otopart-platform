package com.otopart.domain.product.controller;

import com.otopart.domain.product.entity.Product;
import com.otopart.domain.product.service.ProductService;
import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Ürün listeleme ve arama")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Ürün listesi")
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) DeliveryType deliveryType,
            @RequestParam(required = false) Boolean expressOnly,
            @RequestParam(required = false) Boolean discountedOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products;

        if (query != null && !query.isBlank()) {
            products = productService.search(query, pageable);
        } else if (Boolean.TRUE.equals(expressOnly)) {
            products = productService.getExpress(pageable);
        } else if (Boolean.TRUE.equals(discountedOnly)) {
            products = productService.getDiscounted(pageable);
        } else if (deliveryType != null) {
            products = productService.getByDeliveryType(deliveryType, pageable);
        } else if (categoryId != null) {
            products = productService.getByCategory(categoryId, pageable);
        } else {
            products = productService.getAll(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ürün detayı")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Slug ile ürün getir")
    public ResponseEntity<ApiResponse<Product>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug)));
    }

    @GetMapping("/search/tire")
    @Operation(summary = "Lastik ebatına göre arama (üye olmadan)")
    public ResponseEntity<ApiResponse<Page<Product>>> searchByTire(
            @RequestParam String tireSize,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                productService.searchByTireSize(tireSize, PageRequest.of(page, size))));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Ürün oluştur")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Ürün oluşturuldu", productService.create(product)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Ürün sil")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Ürün silindi")
                .build());
    }
}