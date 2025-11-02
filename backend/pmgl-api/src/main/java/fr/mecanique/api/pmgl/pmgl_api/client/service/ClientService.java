package fr.mecanique.api.pmgl.pmgl_api.client.service;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.account.enums.AccountType; // assure-toi d'avoir la valeur pour client (CLIENT/CUSTOMER)
import fr.mecanique.api.pmgl.pmgl_api.account.repositorie.AccountRepository;
import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import fr.mecanique.api.pmgl.pmgl_api.client.keycloak.KeycloakClientService;
import fr.mecanique.api.pmgl.pmgl_api.client.repositorie.ClientRepository;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.CreateQuoteRequestDTO.ApplicantDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestRepository;
import fr.mecanique.api.pmgl.pmgl_api.uuid.service.UuidService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final UuidService uuidService;
    private final QuoteRequestRepository quoteRequestRepository;
    private final KeycloakClientService keycloakClientService;

    /**
     * 1) Si clientId fourni -> retourne le Client existant (404 sinon)
     * 2) Sinon, par email :
     *   - si Account existe et a déjà un Client -> le retourne
     *   - si Account existe sans Client -> crée Client lié à cet Account
     *   - sinon -> crée Account(status=false) + Client
     */
    @Transactional
    public Client resolveOrCreateClient(Long clientId, ApplicantDTO applicant) {
        if (clientId != null) {
            return clientRepository.findById(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));
        }

        // Pas de clientId -> il faut au moins applicant.email
        if (applicant == null || applicant.getEmail() == null) {
            throw new IllegalArgumentException("Email requis pour une première demande de devis.");
        }

        var accountOpt = this.accountRepository.findByEmail(applicant.getEmail());

        Account account = accountOpt.orElseGet(() -> {
            // AccountType a une valeur "CLIENT" (ou "customer")
            Account acc = Account.builder()
                    .accountType(AccountType.CUSTOMER)
                    .firstName(applicant.getFirstName())
                    .name(applicant.getLastName())
                    .email(applicant.getEmail())
                    .active(false)                 // pas encore activé
                    .civility(applicant.getCivility())
                    .referenceAccount(uuidService.generateUuid())
                    .createdAt(LocalDateTime.now())
                    .build();
            return this.accountRepository.save(acc);
        });

        // Si déjà un Client pour cet account -> on le réutilise
        var existingClient = clientRepository.findByAccount_Id(account.getId());
        if (existingClient.isPresent()) return existingClient.get();

        // Sinon on crée le Client
        Client client = Client.builder()
                .account(account)
                .typeClient(applicant.getTypeClient() != null ? applicant.getTypeClient() : TypeClient.particulier)
                .raisonSociale(applicant.getRaisonSociale())
                .siret(applicant.getSiret())
                .adresse(applicant.getAdresse())
                .telephone(applicant.getTelephone())
                .createdAt(LocalDateTime.now())
                .build();

        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClientCompletely(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));

        // 1️⃣ Supprimer toutes les demandes de devis de ce client
        quoteRequestRepository.deleteAllByClientId(clientId);

        // 2️⃣ Supprimer le compte dans Keycloak
        try {
            if (client.getAccount() != null && client.getAccount().getEmail() != null) {
                keycloakClientService.deleteClientUser(client.getAccount().getEmail());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de la suppression Keycloak : " + e.getMessage());
        }

        // 3️⃣ Supprimer l'account local
        Long accountId = client.getAccount().getId();
        accountRepository.deleteById(accountId);

        System.out.println("✅ Client supprimé localement et dans Keycloak : " + client.getAccount().getEmail());
    }
}

