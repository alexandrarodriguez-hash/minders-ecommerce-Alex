package com.minders.backend.event.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload de entrada para el evento de Amplitude "Page Viewed".
 * Event Properties (taxonomía estricta): page_name.
 */
public record PageViewedRequest(
        String userId,
        @NotBlank String page_name
) {
}
