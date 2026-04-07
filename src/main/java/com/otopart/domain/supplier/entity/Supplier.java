package com.otopart.domain.supplier.entity;

import com.otopart.shared.enums.City;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City warehouseCity;

    private String warehouseAddress;

    private String apiUrl;
    private String apiKey;
    private String apiSecret;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ankaraWarehouse = false;

    private String orderCutoffTime;   // "16:00"

    private String contactEmail;
    private String contactPhone;

    @Column(precision = 5, scale = 2)
    private java.math.BigDecimal commissionRate;
}