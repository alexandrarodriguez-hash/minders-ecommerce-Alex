package com.minders.backend.braze;

import com.minders.backend.braze.dto.BrazeTrackRequest;
import com.minders.backend.braze.dto.BrazeTrackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class BrazeClient {

    private static final Logger log = LoggerFactory.getLogger(BrazeClient.class);

    private final RestTemplate restTemplate;

    @Value("${braze.api-key}")
    private String apiKey;

    @Value("${braze.rest-endpoint}")
    private String restEndpoint;

    public BrazeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Envía un lote de eventos/compras a Braze mediante /users/track.
     * No lanza excepción hacia arriba: si Braze falla o no está
     * configurado, se loguea y se devuelve false para no romper
     * el flujo de compra del usuario en el front.
     */
    public boolean track(BrazeTrackRequest request) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("CHANGE_ME")) {
            log.warn("BRAZE_API_KEY no configurada; se omite el envío a Braze. Payload: {}", request);
            return false;
        }

        String url = restEndpoint + "/users/track";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<BrazeTrackRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<BrazeTrackResponse> response =
                    restTemplate.postForEntity(url, entity, BrazeTrackResponse.class);

            BrazeTrackResponse body = response.getBody();
            boolean hasErrors = body != null && body.errors() != null && !body.errors().isEmpty();

            if (response.getStatusCode() != HttpStatus.CREATED || hasErrors) {
                log.error("Braze respondió con problemas. status={} body={}", response.getStatusCode(), body);
                return false;
            }

            log.info("Evento enviado a Braze correctamente: {}", body != null ? body.message() : "OK");
            return true;

        } catch (RestClientException ex) {
            log.error("Error llamando a Braze /users/track: {}", ex.getMessage(), ex);
            return false;
        }
    }
}
