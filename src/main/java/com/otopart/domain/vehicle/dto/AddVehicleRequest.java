package com.otopart.domain.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddVehicleRequest {

    @NotBlank(message = "Sase numarasi zorunludur")
    private String chassisNumber;

    @NotBlank(message = "Marka zorunludur")
    private String brand;

    @NotBlank(message = "Model zorunludur")
    private String model;

    @NotNull(message = "Model yili zorunludur")
    private Integer year;

    private String engineCode;
    private String fuelType;
    private String transmission;
    private String tireSizeFront;
    private String tireSizeRear;
    private String nickname;
}