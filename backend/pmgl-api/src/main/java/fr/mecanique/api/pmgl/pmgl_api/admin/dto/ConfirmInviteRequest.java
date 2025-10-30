package fr.mecanique.api.pmgl.pmgl_api.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmInviteRequest {

    private String token;
    private String password;
}
