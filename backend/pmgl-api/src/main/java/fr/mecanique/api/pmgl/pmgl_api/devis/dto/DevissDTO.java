package fr.mecanique.api.pmgl.pmgl_api.devis.dto;

import fr.mecanique.api.pmgl.pmgl_api.devis.bean.LigneDevis;
import fr.mecanique.api.pmgl.pmgl_api.devis.enums.DevisStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DevissDTO {

    private Integer id;

    private Long clientId;

    private Long adminId;

    private Long quoteRequestId;

    private String numeroDevis;

    private DevisStatut statut = DevisStatut.BROUILLON;

    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateEnvoi;

    private LocalDate dateValidite;

    private LocalDateTime dateModification = LocalDateTime.now();

    private BigDecimal montantHt = BigDecimal.ZERO;

    private BigDecimal tva = new BigDecimal("20.00");

    private String delaiLivraison;

    private String conditions;

    private String fichierDevis;

    private String notesAdmin;

    private List<LigneDevis> lignes;
}
