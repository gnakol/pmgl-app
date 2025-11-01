package fr.mecanique.api.pmgl.pmgl_api.client.service;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.account.repositorie.AccountRepository;
import fr.mecanique.api.pmgl.pmgl_api.client.dto.ConfirmInviteClientRequest;
import fr.mecanique.api.pmgl.pmgl_api.client.keycloak.KeycloakClientService;
import fr.mecanique.api.pmgl.pmgl_api.devis.mail.MailServiceDevis;
import fr.mecanique.api.pmgl.pmgl_api.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientInviteService {

    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final KeycloakClientService keycloakClientService;
    private final MailServiceDevis mailServiceDevis;

    @Transactional
    public void confirmInvitation(ConfirmInviteClientRequest request) {
        Claims claims;
        try {
            claims = jwtService.validateInvitationToken(request.getToken());
        } catch (Exception e) {
            throw new RuntimeException("Token d'invitation invalide ou expiré");
        }

        String email = claims.get("email", String.class);
        String ref = claims.get("ref", String.class);

        Account account = accountRepository.findByReferenceAccount(ref)
                .orElseThrow(() -> new RuntimeException("Aucun compte lié à cette invitation"));

        if (!account.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Incohérence des données d'invitation");
        }

        // 1️⃣ Activer le compte applicatif
        account.setActive(true);
        accountRepository.save(account);

        // 2️⃣ Créer l'utilisateur dans Keycloak
        keycloakClientService.createClientUser(email, request.getPassword());

        // 3️⃣ Sauvegarder le keycloakId
        String keycloakId = keycloakClientService.getUserIdByEmail(email);
        account.setKeycloakId(keycloakId);
        accountRepository.save(account);

        // 4️⃣ 🔥 Envoyer le mail de confirmation au client
        this.mailServiceDevis.notifyAccountActivated(account.getEmail(), account.getFirstName());
    }
}

