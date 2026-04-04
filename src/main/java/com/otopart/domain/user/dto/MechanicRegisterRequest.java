package com.otopart.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MechanicRegisterRequest extends RegisterRequest {

    @NotBlank(message = "Vergi numarası zorunludur")
    private String taxNumber;

    @NotBlank(message = "İşletme adı zorunludur")
    private String companyName;

    private boolean dbsEnabled;
    private String dbsAccountNo;
}