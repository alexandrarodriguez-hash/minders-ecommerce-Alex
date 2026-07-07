package com.minders.backend.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * Payload de entrada para el evento de Amplitude "Order Completed".
 * Event Properties (taxonomía estricta): order_id, revenue.
 *
 * Campos adicionales (customerType, items) NO son Event Properties de
 * Amplitude: se usan solo del lado del backend para actualizar las
 * User Properties (lifetime_value, customer_type, first_purchase_date)
 * y para dar granularidad de producto a Braze via /users/track "purchases"
 * (ver EventTrackingService). Ver comentarios ahí para el detalle.
 */
public record OrderCompletedRequest(
        String userId,
        @NotBlank String order_id,
        @NotNull @PositiveOrZero Double revenue,

        // No es una Event Property de Amplitude. Se usa para poblar la
        // User Property "customer_type" ("guest" | "registered").
        String customerType,

        // No es una Event Property de Amplitude. Opcional: solo se usa
        // para reportar purchases por producto a Braze (revenue/LTV por
        // ítem). Si no se envía, igual se trackea el evento Order Completed.
        @Valid List<OrderItemDto> items
) {
}
