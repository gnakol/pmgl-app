package fr.mecanique.api.pmgl.pmgl_api.admin.dto;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.admin.enums.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDTO {

    private Long idAdmin;

    private String zone;

    private Long accountId;

    private String refAdmins;

    private AdminRole role;

    private Date creationDate;
}
