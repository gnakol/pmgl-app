package fr.mecanique.api.pmgl.pmgl_api.account.bean;

import fr.mecanique.api.pmgl.pmgl_api.account.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_account")
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type_account", columnDefinition = "account_type_enum", nullable = false)
    private AccountType accountType;


    @Column(name = "first_name")
    private String firstName;

    @Column(name = "name")
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "status", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean active;

    @Column(name = "civility")
    private String civility;

    @Column(name = "date_creation", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now(); // Auto-rempli à la création

    @Column(name = "keycloak_id")
    private String keycloakId;

    @Column(name = "ref_account")
    private String referenceAccount;
}