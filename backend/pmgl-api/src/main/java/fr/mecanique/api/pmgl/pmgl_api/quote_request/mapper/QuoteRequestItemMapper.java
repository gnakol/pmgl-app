package fr.mecanique.api.pmgl.pmgl_api.quote_request.mapper;

import fr.mecanique.api.pmgl.pmgl_api.client.mappers.CustomerMapper;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestItem;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.QuoteRequestItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {QuoteRequestMapper.class, CustomerMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface QuoteRequestItemMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "quoteRequestId", source = "quoteRequest.id")
    QuoteRequestItemDTO fromQuoteRequestItem(QuoteRequestItem quoteRequestItem);

    @Mapping(target = "client.id", source = "clientId")
    @Mapping(target = "quoteRequest.id", source = "quoteRequestId")
    QuoteRequestItem fromQuoteRequestItemDTO(QuoteRequestItemDTO quoteRequestItemDTO);
}
