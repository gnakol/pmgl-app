package fr.mecanique.api.pmgl.pmgl_api.quote_request.dto;

import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuoteRequestDTO {
    private Long id;
    private Long clientId;
    private String statut;
    private String notesGlobales;
    private LocalDateTime createdAt;

    private ClientInfoDTO client;
    private List<ItemDTO> items;
    private List<FileDTO> files; // NOUVEAU : ajout des fichiers

    @Data
    public static class ClientInfoDTO {
        private String civility;
        private String firstName;
        private String lastName;
        private String email;
        private String telephone;
        private String adresse;
        private TypeClient typeClient;
        private String raisonSociale;
        private String siret;
    }

    @Data
    public static class ItemDTO {
        private Long id;
        private String nomPiece;
        private String typePiece;
        private String matiere;
        private String dimensions;
        private String tolerance;
        private String finition;
        private String traitement;
        private Integer quantite;
        private LocalDate delaiSouhaite;
        private String descriptionLigne;
        private Boolean urgence;
    }

    // NOUVEAU : DTO pour les fichiers
    @Data
    public static class FileDTO {
        private Long id;
        private String fileName;
        private String fileType;
        private String description;
        private String filePath;
        private String mimeType;
        private Long sizeBytes;
        private LocalDateTime uploadedAt;
        private Integer itemIndex;
        private Long itemId; // ID de l'item lié si applicable
    }
}