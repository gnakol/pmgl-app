package fr.mecanique.api.pmgl.pmgl_api.client.dto;


import lombok.Data;

@Data
public class ConfirmInviteClientRequest {
    private String token;
    private String password;
}

