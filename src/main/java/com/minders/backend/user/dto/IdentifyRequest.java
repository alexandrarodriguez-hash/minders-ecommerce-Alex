package com.minders.backend.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Petición que recibe el endpoint POST /api/users/identify.
 * Fusiona en Braze el historial anónimo (anonymousId) con el
 * external_id real del usuario autenticado (externalId).
 */
public record IdentifyRequest(
        @NotBlank(message = "anonymousId es obligatorio")
        String anonymousId,

        @NotBlank(message = "externalId es obligatorio")
        String externalId
) {}
