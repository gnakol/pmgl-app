package fr.mecanique.api.pmgl.pmgl_api.quote_request.bean;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.enums.FileKind;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "quote_request_file")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuoteRequestFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quote_request_file_id_seq")
    @SequenceGenerator(name = "quote_request_file_id_seq", sequenceName = "quote_request_file_id_seq", allocationSize = 1)
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

    // ON DELETE CASCADE
    @ManyToOne
    @JoinColumn(name = "request_item_id")
    private QuoteRequestItem requestItem;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name = "original_name", length = 255)
    private String originalName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "file_kind", columnDefinition = "file_kind_enum", nullable = false)
    private FileKind fileKind;

    @Column(name = "mime_type", length = 150)
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}

