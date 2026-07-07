package com.minders.backend.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderItemDto(
        @NotBlank String productId,
        @NotBlank String productName,
        @NotNull @PositiveOrZero Double price,
        @NotNull @Positive Integer quantity
) {
}
