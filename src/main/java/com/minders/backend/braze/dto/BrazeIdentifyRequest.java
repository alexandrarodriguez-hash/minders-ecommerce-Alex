package com.minders.backend.braze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Cuerpo del POST a https://{rest-endpoint}/users/identify.
 * Fusiona el perfil anónimo (user alias) con el external_id real.
 * https://www.braze.com/docs/api/endpoints/user_data/post_user_identify/
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BrazeIdentifyRequest(
        List<AliasToIdentify> aliases_to_identify
) {
    public record UserAlias(
            String alias_name,
            String alias_label
    ) {}

    /**
     * @param external_id  El external_id real del usuario autenticado
     * @param user_alias   El objeto alias que contiene el alias_name y alias_label
     */
    public record AliasToIdentify(
            String external_id,
            UserAlias user_alias
    ) {}

    public static BrazeIdentifyRequest of(String anonymousId, String externalId) {
        return new BrazeIdentifyRequest(
                List.of(new AliasToIdentify(externalId, new UserAlias(anonymousId, "anonymous_id")))
        );
    }
}

