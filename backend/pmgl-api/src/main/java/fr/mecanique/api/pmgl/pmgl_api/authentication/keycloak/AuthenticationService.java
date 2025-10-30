package fr.mecanique.api.pmgl.pmgl_api.authentication.keycloak;

import fr.mecanique.api.pmgl.pmgl_api.authentication.bean.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RestTemplate restTemplate;  // ← Utilise celui injecté

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    public String authenticateUser(LoginRequestDTO loginRequest) {
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "pmgl-api");
        body.add("client_secret", "V8NWhFQ6fUhmE4IC66VPzr29cU9bO80G");  // Vérifie que ce secret est correct
        body.add("grant_type", "password");
        body.add("username", loginRequest.getEmail());
        body.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            } else {
                throw new RuntimeException("Échec de l'authentification - Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'authentification: " + e.getMessage());
        }
    }
}
