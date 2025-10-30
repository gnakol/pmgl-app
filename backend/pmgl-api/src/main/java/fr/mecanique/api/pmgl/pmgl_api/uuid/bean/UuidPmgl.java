package fr.mecanique.api.pmgl.pmgl_api.uuid.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "UUI")
public class UuidPmgl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_uuid")
    private Long idUuid;

    @Column(name = "key_generate")
    private String uuidGenerate;
}
