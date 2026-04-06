package com.otopart.domain.product.entity;

import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String slug;
    private String description;
    private String iconUrl;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> subcategories;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private boolean expressCategory = false;  // yağ/bakım

    @Builder.Default
    private boolean specialDelivery = false;  // kaporta vb.
}