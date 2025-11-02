package fr.mecanique.api.pmgl.pmgl_api.quote_request.dto;

import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateQuoteRequestDTO {

    private Long clientId;
    private ApplicantDTO applicant;

    private String notesGlobales;

    @NotNull @Size(min = 1, message = "Au moins une ligne de demande est requise")
    private List<ItemDTO> items;

    // Fichiers techniques (optionnel)
    private List<FileDTO> files;

    @Data
    public static class ApplicantDTO {
        // --- Données de base ---
        @NotBlank private String civility;      // "MR", "MME", etc.
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        @Email @NotBlank private String email;

        // --- Contact & adresse ---
        @NotBlank private String telephone;
        private String adresse;

        // --- Nature du client ---
        @NotNull private TypeClient typeClient; // particulier | entreprise

        // Obligatoires si entreprise
        private String raisonSociale;
        private String siret;
    }

    @Data
    public static class ItemDTO {
        @NotBlank private String nomPiece;
        private String typePiece;
        private String matiere;     // libre -> mappée vers enum
        private String dimensions;
        private String tolerance;
        private String finition;
        private String traitement;

        @NotNull @Positive(message = "quantite > 0")
        private Integer quantite;

        private LocalDate delaiSouhaite;
        private String descriptionLigne;
        private Boolean urgence;
    }

    @Data
    public static class FileDTO {
        private String fileName;    // ex: "support_roulement.step"
        private String fileType;    // "PLAN_2D", "MODELE_3D", "PHOTO", "AUTRE" (FileKind)
        private String description; // libre
        private String contentBase64; // -> évite byte[] en JSON brut
        private Integer itemIndex;  // optionnel: lier à items[i], sinon global
    }
}
