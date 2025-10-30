package fr.mecanique.api.pmgl.pmgl_api.quote_request.bean;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quote_request")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuoteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_request_id_seq")
    @SequenceGenerator(name = "quote_request_id_seq", sequenceName = "quote_request_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id") // nullable = true (ON DELETE SET NULL)
    private Client client;

    @Column(name = "statut", length = 30)
    private String statut;

    @Column(name = "notes_globales")
    private String notesGlobales;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

