package fr.mecanique.api.pmgl.pmgl_api.quote_request.services;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.mail.MailServiceClient;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientService;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestCreatedEvent;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestItem;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.CreateQuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.MatiereType;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestItemRepository;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestRepository;
import fr.mecanique.api.pmgl.pmgl_api.admin.mail.MailServiceAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class QuoteRequestService {

    private final QuoteRequestRepository quoteRequestRepository;
    private final QuoteRequestItemRepository itemRepository;

    private final ClientService clientService;
    private final MailServiceClient mailServiceClient;
    private final MailServiceAdmin mailServiceAdmin;

    private final ApplicationEventPublisher events;

    @Transactional
    public Long createQuoteRequest(@Valid CreateQuoteRequestDTO dto) {

        // 1) Résoudre le client (existant OU création account(status=false)+client)
        Client client = clientService.resolveOrCreateClient(dto.getClientId(), dto.getApplicant());

        // 2) Créer la QuoteRequest (statut par défaut DB = 'NOUVELLE')
        QuoteRequest toSave = QuoteRequest.builder()
                .client(client)
                .statut(null) // DB mettra 'NOUVELLE'
                .notesGlobales(dto.getNotesGlobales())
                .createdAt(LocalDateTime.now())
                .build();

        // IMPORTANT: capturer dans une variable finale (pour l'utiliser dans la boucle)
        final QuoteRequest savedReq = quoteRequestRepository.save(toSave);

        // 3) Lignes
        int itemsCount = 0;
        if (dto.getItems() != null) {
            for (var line : dto.getItems()) {
                MatiereType matiereEnum = mapMatiere(line.getMatiere());

                QuoteRequestItem item = QuoteRequestItem.builder()
                        .client(client)              // NOT NULL en DB
                        .quoteRequest(savedReq)
                        .nomPiece(line.getNomPiece())
                        .typePiece(line.getTypePiece())
                        .matiere(matiereEnum)
                        .dimensions(line.getDimensions())
                        .tolerance(line.getTolerance())
                        .finition(line.getFinition())
                        .traitement(line.getTraitement())
                        .quantite(line.getQuantite())
                        .delaiSouhaite(line.getDelaiSouhaite())
                        .descriptionLigne(line.getDescriptionLigne())
                        .urgence(Boolean.TRUE.equals(line.getUrgence()))
                        .createdAt(LocalDateTime.now())
                        .build();

                itemRepository.save(item);
                itemsCount++;
            }
        }

        // 4) Email client
        String email = client.getAccount().getEmail();
        String firstName = client.getAccount().getFirstName();
       /* mailServiceClient.notifyQuoteRequestReceived(email, firstName);*/

        // 5) Email interne pour Michel (admin)
        /*mailServiceAdmin.notifyStaffNewQuoteRequest(savedReq.getId(), client, itemsCount);*/

        // ✅ Publier l’événement (traité AFTER_COMMIT dans le listener)
        events.publishEvent(new QuoteRequestCreatedEvent(
                savedReq.getId(),
                client.getId(),
                client.getAccount() != null ? client.getAccount().getEmail() : null,
                client.getAccount() != null ? client.getAccount().getFirstName() : null,
                itemsCount
        ));

        return savedReq.getId();
    }

    /**
     * Mappe une chaîne (éventuellement "libre") vers l'enum MatiereType.
     * - Tente d'abord le match exact (respect casse) sur les noms d'enum Java.
     * - Puis un match insensible à la casse.
     * - Puis quelques synonymes "friendly".
     * - Sinon, lève une IllegalArgumentException explicite.
     */
    private MatiereType mapMatiere(String raw) {
        if (raw == null || raw.isBlank()) return null;

        String trimmed = raw.trim();

        // 1) Match exact sur le nom d'enum tel quel
        for (MatiereType m : MatiereType.values()) {
            if (m.name().equals(trimmed)) return m;
        }

        // 2) Match insensible à la casse (ALU_5083 vs alu_5083, etc.)
        for (MatiereType m : MatiereType.values()) {
            if (m.name().equalsIgnoreCase(trimmed)) return m;
        }

        // 3) Synonymes courants / normalisation simple
        String norm = trimmed.toLowerCase().replaceAll("[^a-z0-9_]", "");
        switch (norm) {
            case "alu":
            case "aluminium":
                return MatiereType.aluminium;
            case "acier":
                return MatiereType.acier;
            case "inox":
                return MatiereType.inox;
            case "laiton":
                return MatiereType.laiton;
            case "bronze":
                return MatiereType.bronze;
            case "plastique":
                return MatiereType.plastique;
            case "autre":
                return MatiereType.autre;
            default:
                throw new IllegalArgumentException(
                        "Matière inconnue: " + raw +
                                ". Valeurs autorisées: " + Arrays.toString(MatiereType.values())
                );
        }
    }
}
