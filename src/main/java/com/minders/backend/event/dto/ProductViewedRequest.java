package com.minders.backend.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Payload de entrada para el evento de Amplitude "Product Viewed".
 * Event Properties (taxonomía estricta): product_id, product_name, price.
 */
public record ProductViewedRequest(
        String userId,
        @NotBlank String product_id,
        @NotBlank String product_name,
        @NotNull @PositiveOrZero Double price
) {
}
