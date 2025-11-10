package fr.mecanique.api.pmgl.pmgl_api.client.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
    @SequenceGenerator(name = "client_id_seq", sequenceName = "client_id_client_seq", allocationSize = 1)
    @Column(name = "id_client")
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type_client", columnDefinition = "type_client_enum", nullable = false)
    private TypeClient typeClient;

    @Column(name = "raison_sociale", length = 255)
    private String raisonSociale;

    @Column(name = "siret", length = 14)
    private String siret;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "date_creation")
    private LocalDateTime createdAt;
}

