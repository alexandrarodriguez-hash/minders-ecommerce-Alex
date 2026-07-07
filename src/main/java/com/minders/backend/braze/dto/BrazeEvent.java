package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Representa un evento custom tal como lo espera Braze en /users/track.
 * https://www.braze.com/docs/api/endpoints/user_data/post_user_track/
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BrazeEvent(
        String external_id,
        String name,
        String time,
        Map<String, Object> properties
) {
}
