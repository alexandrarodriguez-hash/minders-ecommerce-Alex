package com.minders.backend.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Payload de entrada para el evento de Amplitude "Product Added to Cart".
 * Event Properties (taxonomía estricta): product_id, quantity.
 *
 * Nota: price/product_name NO forman parte de las Event Properties de este
 * evento según la taxonomía, por lo que no se piden aquí. Si se necesitan
 * para lógica interna (ej. armar el purchase de Braze en Order Completed),
 * se resuelven en ese evento, no en este.
 */
public record ProductAddedToCartRequest(
        String userId,
        @NotBlank String product_id,
        @NotNull @Positive Integer quantity
) {
}
