package fr.mecanique.api.pmgl.pmgl_api.account.controller;

import fr.mecanique.api.pmgl.pmgl_api.account.dto.AccountDTO;
import fr.mecanique.api.pmgl.pmgl_api.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("account")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV', 'ROLE_CUSTOMER')")
    @GetMapping("/get-account-by-id/{idAccount}")
    public ResponseEntity<AccountDTO> getAccountById(@Validated @PathVariable Long idAccount)
    {
        return this.accountService.getAccountById(idAccount)
                .map(accountDTO -> {
                    log.info("Account with ID : " +idAccount+ " was found");
                    return new ResponseEntity<>(accountDTO, HttpStatus.OK);
                })
                .orElseThrow(() -> {
                    log.error("Account with ID : "+idAccount+ " was not found");
                    throw new RuntimeException("Unable to retrieve Account. Please check the provider ID");
                });
    }
}
