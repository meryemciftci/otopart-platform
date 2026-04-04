package com.otopart.domain.user.dto;

import com.otopart.shared.enums.City;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Ad zorunludur")
    private String firstName;

    @NotBlank(message = "Soyad zorunludur")
    private String lastName;

    @Email(message = "Geçerli bir e-posta giriniz")
    @NotBlank(message = "E-posta zorunludur")
    private String email;

    @NotBlank(message = "Telefon zorunludur")
    private String phone;

    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    @NotBlank(message = "Şifre zorunludur")
    private String password;

    private City city;
    private String district;
    private String fullAddress;
    private Double latitude;
    private Double longitude;
}