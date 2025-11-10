package fr.mecanique.api.pmgl.pmgl_api.account.webservice;

import java.util.Optional;

public interface AccountWebservice <T>{

    Optional<T> getAccountById(Long id);
}
