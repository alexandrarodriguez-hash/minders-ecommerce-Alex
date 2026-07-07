package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Representa las User Properties tal como las espera Braze en el array
 * "attributes" de /users/track.
 * https://www.braze.com/docs/api/objects_filters/user_attributes_object/
 *
 * Mapea 1:1 con las User Properties de la taxonomía:
 *  - lifetime_value
 *  - customer_type
 *  - first_purchase_date
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BrazeAttributes(
        String external_id,
        Double lifetime_value,
        String customer_type,
        String first_purchase_date
) {
}
