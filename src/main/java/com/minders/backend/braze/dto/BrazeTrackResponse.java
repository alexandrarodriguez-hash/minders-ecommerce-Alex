package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrazeTrackResponse(
        String message,
        List<String> errors
) {
}
