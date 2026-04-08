package com.otopart.domain.vehicle.entity;

import com.otopart.domain.user.entity.User;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String chassisNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    private String engineCode;
    private String fuelType;
    private String transmission;
    private String tireSizeFront;
    private String tireSizeRear;
    private String nickname;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDefault = false;
}