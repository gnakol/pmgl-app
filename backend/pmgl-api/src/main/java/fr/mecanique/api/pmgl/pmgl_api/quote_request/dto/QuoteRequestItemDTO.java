package fr.mecanique.api.pmgl.pmgl_api.quote_request.dto;

import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.MatiereType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteRequestItemDTO {

    private Long id;

    private Long clientId;

    private Long quoteRequestId;

    private String nomPiece;

    private String typePiece;

    private MatiereType matiere;

    private String dimensions;

    private String tolerance;

    private String finition;

    private String traitement;

    private Integer quantite;

    private LocalDate delaiSouhaite;

    private String descriptionLigne;

    private Boolean urgence;

    private LocalDateTime createdAt;
}
