package fr.mecanique.api.pmgl.pmgl_api.account.service;

import fr.mecanique.api.pmgl.pmgl_api.account.dto.AccountDTO;
import fr.mecanique.api.pmgl.pmgl_api.account.mappers.AccountMapper;
import fr.mecanique.api.pmgl.pmgl_api.account.repositorie.AccountRepository;
import fr.mecanique.api.pmgl.pmgl_api.account.webservice.AccountWebservice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountWebservice<AccountDTO> {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;


    @Override
    public Optional<AccountDTO> getAccountById(Long id) {
        return this.accountRepository.findById(id)
                .map(this.accountMapper::fromAccount);
    }
}
