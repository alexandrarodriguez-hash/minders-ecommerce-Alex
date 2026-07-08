package com.minders.backend.user;

import com.minders.backend.braze.BrazeClient;
import com.minders.backend.braze.dto.BrazeAttributes;
import com.minders.backend.braze.dto.BrazeTrackRequest;
import com.minders.backend.user.dto.IdentifyRequest;
import org.springframework.stereotype.Service;

/**
 * Gestiona la identificación de usuarios: fusiona el perfil anónimo
 * del visitante con su external_id real una vez que se autentica.
 */
@Service
public class UserService {

    private final BrazeClient brazeClient;

    public UserService(BrazeClient brazeClient) {
        this.brazeClient = brazeClient;
    }

    /**
     * Llama a Braze /users/identify para unificar el historial
     * anónimo (anonymousId) con el perfil permanente (externalId).
     * Después de esto, todos los eventos futuros del usuario ya
     * van con el external_id real, y Braze asocia el historial previo.
     * Si el request incluye un correo, lo guarda como atributo del usuario.
     *
     * @return true si Braze confirmó la operación, false si Braze no
     *         está configurado o devolvió error (nunca lanza excepción).
     */
    public boolean identify(IdentifyRequest request) {
        boolean identified = brazeClient.identify(request.anonymousId(), request.externalId());

        if (request.email() != null && !request.email().isBlank()) {
            BrazeAttributes attributes = new BrazeAttributes(
                    request.externalId(),
                    null, // lifetime_value
                    "registered", // customer_type
                    null, // first_purchase_date
                    request.email().trim() // email
            );
            brazeClient.track(BrazeTrackRequest.ofAttributes(attributes));
        }

        return identified;
    }
}
