package fr.mecanique.api.pmgl.pmgl_api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteAdminRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String zone;
    private String civility;
}
