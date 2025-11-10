package fr.mecanique.api.pmgl.pmgl_api.client.dto;

import fr.mecanique.api.pmgl.pmgl_api.client.enums.TypeClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;

    private Long accountId;

    private TypeClient typeClient;

    private String raisonSociale;

    private String siret;

    private String adresse;

    private String telephone;

    private LocalDateTime createdAt;
}
