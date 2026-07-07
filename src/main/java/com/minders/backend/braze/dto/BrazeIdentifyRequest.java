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
    /**
     * @param alias_name   El anonymousId que usaba el front (ej. "anon-uuid")
     * @param alias_label  Etiqueta del alias; por convenio usamos "anonymous_id"
     * @param external_id  El external_id real del usuario autenticado
     */
    public record AliasToIdentify(
            String alias_name,
            String alias_label,
            String external_id
    ) {}

    public static BrazeIdentifyRequest of(String anonymousId, String externalId) {
        return new BrazeIdentifyRequest(
                List.of(new AliasToIdentify(anonymousId, "anonymous_id", externalId))
        );
    }
}
