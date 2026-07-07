package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Representa una línea de compra tal como la espera Braze en /users/track.
 * Cada producto comprado se reporta como un purchase independiente, lo que
 * permite a Braze calcular revenue, LTV y segmentar por producto comprado.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BrazePurchase(
        String external_id,
        String product_id,
        String currency,
        double price,
        Integer quantity,
        String time,
        Map<String, Object> properties
) {
}
