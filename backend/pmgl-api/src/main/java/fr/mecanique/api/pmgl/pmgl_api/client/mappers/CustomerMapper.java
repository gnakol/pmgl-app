package fr.mecanique.api.pmgl.pmgl_api.client.mappers;

import fr.mecanique.api.pmgl.pmgl_api.account.mappers.AccountMapper;
import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {AccountMapper.class}, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "accountId", source = "account.id")
    CustomerDTO fromClient(Client client);

    @Mapping(target = "account.id", source = "accountId")
    Client fromCustomerDTO(CustomerDTO customerDTO);
}
