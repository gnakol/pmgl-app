package fr.mecanique.api.pmgl.pmgl_api.admin.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.admin.enums.AdminRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long idAdmin;

    @Column(name = "notes")
    private String zone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "ref_admin")
    private String refAdmins;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "role", columnDefinition = "admin_role_enum", nullable = false)
    private AdminRole role;

    @Column(name = "date_creation")
    private Date creationDate;
}