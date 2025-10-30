package fr.mecanique.api.pmgl.pmgl_api.authentication.controller;

import fr.mecanique.api.pmgl.pmgl_api.admin.keycloak.KeycloakAdminService;
import fr.mecanique.api.pmgl.pmgl_api.authentication.bean.LoginRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.authentication.keycloak.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("authenticate")
public class AuthController {

    private final AuthenticationService authenticateUser;
    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/login-dev")
    public ResponseEntity<Map<String, String>> loginDev(@RequestBody LoginRequestDTO loginRequest) {
        String token = this.authenticateUser.authenticateUser(loginRequest);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Identifiants invalides"));
        }

        // ✅ Vérifie si le token contient l'un des rôles autorisés
        if (!this.keycloakAdminService.hasRealmRole(token, "DEV")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé au développeur."));
        }


        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login-admin")
    public ResponseEntity<Map<String, String>> loginAdmin(@RequestBody LoginRequestDTO loginRequest) {
        String token = this.authenticateUser.authenticateUser(loginRequest);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Identifiants invalides"));
        }

        // ✅ Vérifie si le token contient l'un des rôles autorisés
        if (!this.keycloakAdminService.hasRealmRole(token, "ADMIN") &&
                !this.keycloakAdminService.hasRealmRole(token, "SUPER-ADMIN")) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Accès réservé aux administrateurs."));
        }

        return ResponseEntity.ok(Map.of("token", token));
    }
}
