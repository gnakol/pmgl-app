package fr.mecanique.api.pmgl.pmgl_api.client.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByAccount_Id(Long accountId);

    @Query("SELECT c.id FROM Client c WHERE c.account.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}

