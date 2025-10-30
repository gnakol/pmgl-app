package fr.mecanique.api.pmgl.pmgl_api.client.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByAccount_Id(Long accountId);
}

