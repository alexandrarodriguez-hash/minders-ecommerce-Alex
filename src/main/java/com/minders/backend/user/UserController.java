package com.minders.backend.user;

import com.minders.backend.user.dto.IdentifyRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Gestiona operaciones de usuario: identificación, fusión de perfiles, etc.
 *
 * POST /api/users/identify
 *   Funde el historial anónimo del visitante con su external_id real
 *   una vez que se autentica. Llama a Braze /users/identify para que
 *   el historial previo del visitante quede asociado al perfil permanente.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/identify")
    public ResponseEntity<Map<String, Object>> identify(@Valid @RequestBody IdentifyRequest request) {
        boolean sent = userService.identify(request);
        return ResponseEntity.ok(Map.of(
                "externalId", request.externalId(),
                "sentToBraze", sent
        ));
    }
}
