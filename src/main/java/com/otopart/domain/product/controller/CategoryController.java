package com.otopart.domain.product.controller;

import com.otopart.domain.product.entity.Category;
import com.otopart.domain.product.service.CategoryService;
import com.otopart.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Kategori işlemleri")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Ana kategorileri listele")
    public ResponseEntity<ApiResponse<List<Category>>> getMainCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getMainCategories()));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Alt kategorileri listele")
    public ResponseEntity<ApiResponse<List<Category>>> getSubcategories(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getSubcategories(id)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Kategori detayı")
    public ResponseEntity<ApiResponse<Category>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Kategori oluştur")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success("Kategori oluşturuldu", categoryService.create(category)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[Admin] Kategori sil")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Kategori silindi")
                .build());
    }
}