package fr.mecanique.api.pmgl.pmgl_api.account.dto;

import fr.mecanique.api.pmgl.pmgl_api.account.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO {

    private Long id;

    private AccountType accountType;

    private String firstName;

    private String name;

    private String email;

    private Boolean active;

    private String civility;

    private LocalDateTime createdAt = LocalDateTime.now(); // Auto-rempli à la création

    private String keycloakId;

    private String referenceAccount;
}
