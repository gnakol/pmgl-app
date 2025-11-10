package fr.mecanique.api.pmgl.pmgl_api.quote_request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteRequestOTD {

    private Long id;

    private Long clientId;

    private String statut;

    private String notesGlobales;

    private LocalDateTime createdAt;
}
