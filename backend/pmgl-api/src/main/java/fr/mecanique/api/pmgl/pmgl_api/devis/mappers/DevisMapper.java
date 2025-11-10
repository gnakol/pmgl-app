package fr.mecanique.api.pmgl.pmgl_api.devis.mappers;

import fr.mecanique.api.pmgl.pmgl_api.admin.mappers.AdminMapper;
import fr.mecanique.api.pmgl.pmgl_api.client.mappers.CustomerMapper;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import fr.mecanique.api.pmgl.pmgl_api.devis.dto.DevissDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.mapper.QuoteRequestMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, AdminMapper.class, QuoteRequestMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DevisMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "quoteRequestId", source = "quoteRequest.id")
    @Mapping(target = "adminId", source = "admin.idAdmin")
    DevissDTO fromDevis(Devis devis);

    @Mapping(target = "client.id", source = "clientId")
    @Mapping(target = "quoteRequest.id", source = "quoteRequestId")
    @Mapping(target = "admin.idAdmin", source = "adminId")
    Devis fromDevisDTO(DevissDTO devissDTO);
}
