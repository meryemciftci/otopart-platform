package com.otopart.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "Siparis kalemleri bos olamaz")
    private List<OrderItemRequest> items;

    private Long vehicleId;

    @NotBlank(message = "Teslimat adresi zorunludur")
    private String deliveryAddress;

    private String deliveryCity;

    private String couponCode;

    @NotBlank(message = "Odeme yontemi zorunludur")
    private String paymentMethod;

    private Integer installmentCount;

    private String customerNote;

    private String deliverySlot;  // "09:00", "12:00", "15:00", "17:00"
}