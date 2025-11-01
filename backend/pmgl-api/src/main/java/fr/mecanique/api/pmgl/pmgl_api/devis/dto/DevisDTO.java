package fr.mecanique.api.pmgl.pmgl_api.devis.dto;

import fr.mecanique.api.pmgl.pmgl_api.devis.enums.DevisStatut;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DevisDTO {
    private Integer id;
    private String numeroDevis;
    private DevisStatut statut;
    private LocalDateTime dateCreation;
    private LocalDate dateValidite;
    private BigDecimal montantHt;
    private BigDecimal tva;
    private BigDecimal montantTtc;
    private String delaiLivraison;

    private ClientInfoDTO client;
    private AdminInfoDTO admin;
    private List<LigneDevisDTO> lignes;

    @Data
    public static class ClientInfoDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String raisonSociale;
    }

    @Data
    public static class AdminInfoDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
    }

    @Data
    public static class LigneDevisDTO {
        private Integer id;
        private String descriptionLigne;
        private Integer quantite;
        private BigDecimal prixUnitaireHt;
        private BigDecimal totalLigneHt;
        private String matiere;
        private String dimensions;
        private String delaiFabrication;
        private String notes;
    }
}
