package fr.mecanique.api.pmgl.pmgl_api.uuid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UuidDTO {

    private Long idUuid;

    private String uuidGenerate;
}
