package com.otopart.domain.product.entity;

import com.otopart.shared.enums.DeliveryType;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku;

    private String barcode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private com.otopart.domain.supplier.entity.Supplier supplier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private BigDecimal discountedPrice;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    private String tireSize;

    @Builder.Default
    private boolean requiresLargeVehicle = false;

    @Builder.Default
    private boolean expressAvailable = false;

    private String slug;
    private String imageUrl;
    private String supplierProductCode;
}