package com.otopart.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull(message = "Urun ID zorunludur")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Miktar en az 1 olmalidir")
    private Integer quantity;
}