package fr.mecanique.api.pmgl.pmgl_api.quote_request.dto;

import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateQuoteRequestDTO {

    // Si présent => on réutilise ce client. Sinon on crée compte+client avec "applicant"
    private Long clientId;

    // Renseigné si clientId est null (nouveau prospect)
    private ApplicantDTO applicant;

    private String notesGlobales;

    @NotNull @Size(min = 1, message = "Au moins une ligne de demande est requise")
    private List<ItemDTO> items;

    @Data
    public static class ApplicantDTO {
        // --- Données de base ---
        @NotBlank private String civility;      // MR / MME (ton format)
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        @Email @NotBlank private String email;

        // --- Contact & adresse ---
        @NotBlank private String telephone;
        private String adresse;

        // --- Nature du client ---
        @NotNull private TypeClient typeClient; // particulier | entreprise
        private String raisonSociale;           // si entreprise
        private String siret;                   // si entreprise
    }

    @Data
    public static class ItemDTO {
        @NotBlank private String nomPiece;
        private String typePiece;
        private String matiere;
        private String dimensions;
        private String tolerance;
        private String finition;
        private String traitement;

        @NotNull @Positive(message = "quantite > 0") private Integer quantite;

        private LocalDate delaiSouhaite;
        private String descriptionLigne;
        private Boolean urgence;
    }
}


