package com.otopart.domain.user.entity;

import com.otopart.shared.enums.City;
import com.otopart.shared.enums.UserRole;
import com.otopart.shared.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private City city;

    private String district;
    private String fullAddress;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    @Builder.Default
    private Integer loyaltyPoints = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrderCount = 0;

    // B2B / Usta
    private String taxNumber;
    private String companyName;
    private Boolean dbsEnabled;
    private String dbsAccountNo;

    @Builder.Default
    private boolean emailVerified = false;
    private String emailVerificationToken;
    private String refreshToken;
}