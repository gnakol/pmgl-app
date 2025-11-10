package fr.mecanique.api.pmgl.pmgl_api.quote_request.mapper;

import fr.mecanique.api.pmgl.pmgl_api.client.mappers.CustomerMapper;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.QuoteRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface QuoteRequestMapper {

    @Mapping(target = "clientId", source = "client.id")
    QuoteRequestDTO fromQuoteRequest(QuoteRequest quoteRequest);

    @Mapping(target = "client.id", source = "clientId")
    QuoteRequest fromQuoteRequestDTO(QuoteRequestDTO quoteRequestDTO);
}
