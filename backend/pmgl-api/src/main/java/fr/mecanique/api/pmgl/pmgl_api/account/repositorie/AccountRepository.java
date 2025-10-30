package fr.mecanique.api.pmgl.pmgl_api.account.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByKeycloakId(String keycloakId);

    Optional<Account> findByReferenceAccount(String refAccount);

}
