package fr.mecanique.api.pmgl.pmgl_api.quote_request.bean;


import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.MatiereType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quote_request_item")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuoteRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_request_item_id_seq")
    @SequenceGenerator(name = "quote_request_item_id_seq", sequenceName = "quote_request_item_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    // ON DELETE CASCADE
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // ON DELETE CASCADE
    @ManyToOne
    @JoinColumn(name = "quote_request_id")
    private QuoteRequest quoteRequest;

    @Column(name = "nom_piece", length = 255, nullable = false)
    private String nomPiece;

    @Column(name = "type_piece", length = 120)
    private String typePiece;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "matiere", columnDefinition = "matiere_type")
    private MatiereType matiere;

    @Column(name = "dimensions", length = 120)
    private String dimensions;

    @Column(name = "tolerance", length = 60)
    private String tolerance;

    @Column(name = "finition", length = 120)
    private String finition;

    @Column(name = "traitement", length = 120)
    private String traitement;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "delai_souhaite")
    private LocalDate delaiSouhaite;

    @Column(name = "description_ligne")
    private String descriptionLigne;

    @Column(name = "urgence")
    private Boolean urgence;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

