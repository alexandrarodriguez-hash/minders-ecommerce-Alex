package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Cuerpo completo del POST a https://{rest-endpoint}/users/track
 * Braze acepta en un mismo call: eventos custom, purchases y
 * atributos de usuario (User Properties).
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrazeTrackRequest(
        List<BrazeEvent> events,
        List<BrazePurchase> purchases,
        List<BrazeAttributes> attributes
) {
    public static BrazeTrackRequest ofEvent(BrazeEvent event) {
        return new BrazeTrackRequest(List.of(event), List.of(), List.of());
    }

    public static BrazeTrackRequest ofEventAndAttributes(BrazeEvent event, BrazeAttributes attributes) {
        return new BrazeTrackRequest(List.of(event), List.of(), List.of(attributes));
    }

    public static BrazeTrackRequest ofEventPurchasesAndAttributes(
            BrazeEvent event, List<BrazePurchase> purchases, BrazeAttributes attributes) {
        return new BrazeTrackRequest(List.of(event), purchases, List.of(attributes));
    }

    public static BrazeTrackRequest ofAttributes(BrazeAttributes attributes) {
        return new BrazeTrackRequest(List.of(), List.of(), List.of(attributes));
    }
}
