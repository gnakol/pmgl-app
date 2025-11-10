package fr.mecanique.api.pmgl.pmgl_api.account.mappers;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.account.dto.AccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    AccountDTO fromAccount(Account account);

    Account fromAccountDTO(AccountDTO accountDTO);
}
