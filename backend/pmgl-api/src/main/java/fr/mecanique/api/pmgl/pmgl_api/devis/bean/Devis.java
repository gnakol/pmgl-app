package fr.mecanique.api.pmgl.pmgl_api.devis.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.devis.enums.DevisStatut;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "devis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devis")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin; // Michel ou un autre admin

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_id")
    private QuoteRequest quoteRequest;

    @Column(name = "numero_devis", nullable = false, unique = true)
    private String numeroDevis;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private DevisStatut statut = DevisStatut.BROUILLON;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "date_validite")
    private LocalDate dateValidite;

    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();

    @Column(name = "montant_ht")
    private BigDecimal montantHt = BigDecimal.ZERO;

    @Column(name = "tva")
    private BigDecimal tva = new BigDecimal("20.00");

    @Column(name = "delai_livraison")
    private String delaiLivraison;

    @Column(name = "conditions", columnDefinition = "text")
    private String conditions;

    @Column(name = "fichier_devis")
    private String fichierDevis;

    @Column(name = "notes_admin", columnDefinition = "text")
    private String notesAdmin;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDevis> lignes;
}

