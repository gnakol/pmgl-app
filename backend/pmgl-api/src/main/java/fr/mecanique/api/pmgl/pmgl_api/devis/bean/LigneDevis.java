package fr.mecanique.api.pmgl.pmgl_api.devis.bean;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ligne_devis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneDevis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devis", nullable = false)
    private Devis devis;

    @Column(name = "description_ligne", nullable = false)
    private String descriptionLigne;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire_ht", nullable = false)
    private BigDecimal prixUnitaireHt;

    @Column(name = "matiere")
    private String matiere;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "delai_fabrication")
    private String delaiFabrication;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

