package com.otopart.domain.product.repository;

import com.otopart.domain.product.entity.Product;
import com.otopart.shared.enums.DeliveryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);
    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByDeliveryTypeAndActiveTrue(DeliveryType deliveryType, Pageable pageable);

    Page<Product> findByExpressAvailableTrueAndActiveTrue(Pageable pageable);

    Page<Product> findByTireSizeContainingIgnoreCaseAndActiveTrue(String tireSize, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')))
          AND p.active = true
        """)
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.discountedPrice IS NOT NULL AND p.active = true")
    Page<Product> findDiscountedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true")
    Page<Product> findCompatibleProducts(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("year") Integer year,
            Pageable pageable);
}