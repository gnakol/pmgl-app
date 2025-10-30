package fr.mecanique.api.pmgl.pmgl_api.admin.keycloak;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.server-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    public void createAdminUser(String email, String password) {
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

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec de la création de l'utilisateur dans Keycloak");
        }

        // 🔎 Étape 1 : Récupérer l'ID utilisateur
        ResponseEntity<List> userResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?username=" + email,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        List<Map<String, Object>> users = userResponse.getBody();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé après création");
        }

        String userId = (String) users.get(0).get("id");

        // 🏷️ Étape 2 : Récupérer le rôle ADMIN
        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/ADMIN",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        Map<String, Object> adminRole = roleResponse.getBody();

        // 🎯 Étape 3 : Assigner le rôle ADMIN à l'utilisateur
        HttpEntity<List<Map<String, Object>>> assignRequest = new HttpEntity<>(List.of(adminRole), headers);

        restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                HttpMethod.POST,
                assignRequest,
                Void.class
        );
    }

    private String obtainAccessToken() {
        // Requête pour obtenir un access_token admin
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");
        form.add("username", adminUsername);
        form.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public boolean hasRealmRole(String jwtToken, String expectedRole) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(jwtToken.split("\\.")[0] + "." + jwtToken.split("\\.")[1] + ".")
                    .getBody();

            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            List<String> roles = (List<String>) realmAccess.get("roles");

            return roles.contains(expectedRole);
        } catch (Exception e) {
            return false;
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

    public void deleteAdminByEmail(String email) {
        String token = obtainAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // 1. Trouver l'utilisateur par email
        ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        List<Map<String, Object>> users = response.getBody();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("Utilisateur Keycloak introuvable avec l'email : " + email);
        }

        String userId = (String) users.get(0).get("id");

        // 2. Supprimer l'utilisateur
        restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
    }
}
