package fr.mecanique.api.pmgl.pmgl_api.client.keycloak;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.server-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    /** Crée l’utilisateur client dans Keycloak avec mot de passe et (si présent) le rôle CUSTOMER. */
    public void createClientUser(String email, String password) {
        String accessToken = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Création de l'utilisateur
        Map<String, Object> payload = Map.of(
                "username", email,
                "email", email,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", password,
                        "temporary", false
                ))
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users",
                new HttpEntity<>(payload, headers),
                Void.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la création de l'utilisateur client dans Keycloak");
        }

        // Récupérer l'ID utilisateur
        String userId = getUserIdByEmail(email);

        // Récupérer le rôle CUSTOMER (si vous l’avez créé dans le realm)
        try {
            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/roles/CUSTOMER",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );
            Map<String, Object> role = roleResponse.getBody();
            if (role != null) {
                HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(List.of(role), headers);
                restTemplate.exchange(
                        keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                        HttpMethod.POST,
                        assignRequest,
                        Void.class
                );
            }
        } catch (Exception ignoreIfRoleMissing) {
            // Si le rôle n’existe pas, on n’échoue pas le parcours client
        }
    }

    public String getUserIdByEmail(String email) {
        String token = obtainAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        List<Map<String, Object>> users = response.getBody();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("Utilisateur Keycloak non trouvé pour l’email : " + email);
        }
        return (String) users.get(0).get("id");
    }

    private String obtainAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        org.springframework.util.MultiValueMap<String, String> form = new org.springframework.util.LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/realms/master/protocol/openid-connect/token",
                new HttpEntity<>(form, headers),
                Map.class
        );
        return (String) response.getBody().get("access_token");
    }

    public void deleteClientUser(String email) {
        try {
            String token = obtainAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // Cherche l’utilisateur dans Keycloak
            ResponseEntity<List> response = restTemplate.exchange(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    List.class
            );

            List<Map<String, Object>> users = response.getBody();
            if (users == null || users.isEmpty()) {
                System.out.println("⚠️ Aucun utilisateur Keycloak trouvé pour : " + email);
                return;
            }

            // Récupère l'ID et supprime
            String userId = (String) users.get(0).get("id");
            restTemplate.exchange(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                    HttpMethod.DELETE,
                    new HttpEntity<>(headers),
                    Void.class
            );

            System.out.println("✅ Utilisateur Keycloak supprimé : " + email);
        } catch (Exception e) {
            System.err.println("❌ Erreur suppression Keycloak pour " + email + " : " + e.getMessage());
        }
    }

}
