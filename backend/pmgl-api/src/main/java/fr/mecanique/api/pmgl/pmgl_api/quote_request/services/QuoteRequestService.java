package fr.mecanique.api.pmgl.pmgl_api.quote_request.services;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.mail.MailServiceClient;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientService;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestCreatedEvent;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestFile;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestItem;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.CreateQuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.QuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.FileKind;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.MatiereType;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestFileRepository;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestItemRepository;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestRepository;
import fr.mecanique.api.pmgl.pmgl_api.admin.mail.MailServiceAdmin;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class QuoteRequestService {

    private final QuoteRequestRepository quoteRequestRepository;
    private final QuoteRequestItemRepository itemRepository;
    private final QuoteRequestFileRepository fileRepository;

    private final ClientService clientService;
    private final ApplicationEventPublisher events;
    private final FileStorageService fileStorage;

    @Transactional
    public Long createQuoteRequest(@Valid CreateQuoteRequestDTO dto) {

        // 0) Gardes-fous pour "entreprise"
        if (dto.getClientId() == null && dto.getApplicant() != null
                && dto.getApplicant().getTypeClient() != null
                && dto.getApplicant().getTypeClient().name().equals("entreprise")) {
            if (!StringUtils.hasText(dto.getApplicant().getRaisonSociale())
                    || !StringUtils.hasText(dto.getApplicant().getSiret())) {
                throw new IllegalArgumentException("Pour un client 'entreprise', 'raisonSociale' et 'siret' sont requis.");
            }
        }

        // 1) Résoudre le client (existant OU création)
        Client client = clientService.resolveOrCreateClient(dto.getClientId(), dto.getApplicant());

        // 2) Créer la QuoteRequest (statut DB par défaut: 'NOUVELLE')
        QuoteRequest toSave = QuoteRequest.builder()
                .client(client)
                .statut(null) // DB DEFAULT
                .notesGlobales(dto.getNotesGlobales())
                .createdAt(LocalDateTime.now())
                .build();

        final QuoteRequest savedReq = quoteRequestRepository.save(toSave);

        // 3) Lignes — on garde les entités sauvegardées pour lier des fichiers par index
        List<QuoteRequestItem> savedItems = new ArrayList<>();
        if (dto.getItems() != null) {
            for (var line : dto.getItems()) {
                MatiereType matiereEnum = mapMatiere(line.getMatiere());

                QuoteRequestItem item = QuoteRequestItem.builder()
                        .client(client)
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

                savedItems.add(itemRepository.save(item));
            }
        }

        // 4) Fichiers — globaux ou rattachés à une ligne (par itemIndex)
        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (var f : dto.getFiles()) {
                if (!StringUtils.hasText(f.getContentBase64())) continue; // ignorer vide

                FileKind kind = mapFileKind(f.getFileType());
                FileStorageService.StoredFile stored;
                try {
                    stored = fileStorage.storeBase64(
                            savedReq.getId(),
                            f.getFileName(),
                            f.getContentBase64()
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException("Échec du stockage du fichier: " +
                            (f.getFileName() != null ? f.getFileName() : "inconnu") + " — " + e.getMessage());
                }

                QuoteRequestItem linkedItem = null;
                if (f.getItemIndex() != null) {
                    int idx = f.getItemIndex();
                    if (idx < 0 || idx >= savedItems.size()) {
                        throw new IllegalArgumentException("itemIndex invalide pour le fichier '" +
                                f.getFileName() + "': " + idx);
                    }
                    linkedItem = savedItems.get(idx);
                }

                QuoteRequestFile fileEntity = QuoteRequestFile.builder()
                        .client(client)
                        .quoteRequest(savedReq)
                        .requestItem(linkedItem)
                        .filePath(stored.path())
                        .originalName(stored.originalName())
                        .fileKind(kind)
                        .mimeType(stored.mimeType())
                        .sizeBytes(stored.sizeBytes())
                        .uploadedAt(LocalDateTime.now())
                        .build();

                fileRepository.save(fileEntity);
            }
        }

        // 5) Événement (AFTER_COMMIT)
        events.publishEvent(new QuoteRequestCreatedEvent(
                savedReq.getId(),
                client.getId(),
                client.getAccount() != null ? client.getAccount().getEmail() : null,
                client.getAccount() != null ? client.getAccount().getFirstName() : null,
                savedItems.size()
        ));

        return savedReq.getId();
    }

    private FileKind mapFileKind(String raw) {
        if (!StringUtils.hasText(raw)) return FileKind.AUTRE;
        String norm = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return FileKind.valueOf(norm);
        } catch (IllegalArgumentException e) {
            // Petits synonymes tolérés
            return switch (norm) {
                case "STEP", "STP", "3D" -> FileKind.MODELE_3D;
                case "PDF", "DWG", "DXF", "PLAN" -> FileKind.PLAN_2D;
                case "IMG", "IMAGE" -> FileKind.PHOTO;
                default -> FileKind.AUTRE;
            };
        }
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

    public Page<QuoteRequestDTO> getAllQuoteRequests(Pageable pageable) {
        Page<QuoteRequest> quoteRequests = quoteRequestRepository.findAll(pageable);
        return quoteRequests.map(this::mapToDTO);
    }

    public QuoteRequestDTO getQuoteRequestByClientId(Long clientId) {
        QuoteRequest quoteRequest = quoteRequestRepository.findAll().stream()
                .filter(qr -> qr.getClient() != null && qr.getClient().getId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Aucune demande de devis trouvée pour le client: " + clientId));

        return mapToDTO(quoteRequest);
    }

    private QuoteRequestDTO mapToDTO(QuoteRequest quoteRequest) {
        QuoteRequestDTO dto = new QuoteRequestDTO();
        dto.setId(quoteRequest.getId());
        dto.setStatut(quoteRequest.getStatut());
        dto.setNotesGlobales(quoteRequest.getNotesGlobales());
        dto.setCreatedAt(quoteRequest.getCreatedAt());

        if (quoteRequest.getClient() != null) {
            dto.setClientId(quoteRequest.getClient().getId());
            QuoteRequestDTO.ClientInfoDTO clientInfo = new QuoteRequestDTO.ClientInfoDTO();
            clientInfo.setCivility(quoteRequest.getClient().getAccount().getCivility());
            clientInfo.setFirstName(quoteRequest.getClient().getAccount().getFirstName());
            clientInfo.setLastName(quoteRequest.getClient().getAccount().getName());
            clientInfo.setEmail(quoteRequest.getClient().getAccount().getEmail());
            clientInfo.setTelephone(quoteRequest.getClient().getTelephone());
            clientInfo.setAdresse(quoteRequest.getClient().getAdresse());
            clientInfo.setTypeClient(quoteRequest.getClient().getTypeClient());
            clientInfo.setRaisonSociale(quoteRequest.getClient().getRaisonSociale());
            clientInfo.setSiret(quoteRequest.getClient().getSiret());
            dto.setClient(clientInfo);
        }

        // Récupérer les items
        List<QuoteRequestDTO.ItemDTO> items = itemRepository.findByQuoteRequest_Id(quoteRequest.getId().intValue())
                .stream()
                .map(this::mapItemToDTO)
                .toList();
        dto.setItems(items);

        // NOUVEAU : Récupérer les fichiers
        List<QuoteRequestDTO.FileDTO> files = fileRepository.findByQuoteRequest_Id(quoteRequest.getId())
                .stream()
                .map(this::mapFileToDTO)
                .toList();
        dto.setFiles(files);

        return dto;
    }

    private QuoteRequestDTO.ItemDTO mapItemToDTO(QuoteRequestItem item) {
        QuoteRequestDTO.ItemDTO itemDTO = new QuoteRequestDTO.ItemDTO();
        itemDTO.setId(item.getId());
        itemDTO.setNomPiece(item.getNomPiece());
        itemDTO.setTypePiece(item.getTypePiece());
        itemDTO.setMatiere(item.getMatiere() != null ? item.getMatiere().name() : null);
        itemDTO.setDimensions(item.getDimensions());
        itemDTO.setTolerance(item.getTolerance());
        itemDTO.setFinition(item.getFinition());
        itemDTO.setTraitement(item.getTraitement());
        itemDTO.setQuantite(item.getQuantite());
        itemDTO.setDelaiSouhaite(item.getDelaiSouhaite());
        itemDTO.setDescriptionLigne(item.getDescriptionLigne());
        itemDTO.setUrgence(item.getUrgence());
        return itemDTO;
    }

    // NOUVEAU : Mapper pour les fichiers
    private QuoteRequestDTO.FileDTO mapFileToDTO(QuoteRequestFile file) {
        QuoteRequestDTO.FileDTO fileDTO = new QuoteRequestDTO.FileDTO();
        fileDTO.setId(file.getId());
        fileDTO.setFileName(file.getOriginalName());
        fileDTO.setFileType(file.getFileKind() != null ? file.getFileKind().name() : null);
        fileDTO.setFilePath(file.getFilePath());
        fileDTO.setMimeType(file.getMimeType());
        fileDTO.setSizeBytes(file.getSizeBytes());
        fileDTO.setUploadedAt(file.getUploadedAt());
        fileDTO.setItemId(file.getRequestItem() != null ? file.getRequestItem().getId() : null);
        return fileDTO;
    }
}
