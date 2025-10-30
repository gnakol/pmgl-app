package fr.mecanique.api.pmgl.pmgl_api.uuid.mapper;

import fr.mecanique.api.pmgl.pmgl_api.uuid.bean.UuidPmgl;
import fr.mecanique.api.pmgl.pmgl_api.uuid.dto.UuidDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UuidMapper {

    UuidDTO fromUuid(UuidPmgl uuid);

    UuidPmgl fromUuidDTO(UuidDTO uuidDTO);
}
