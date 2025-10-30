package fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories;

import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {

    @Modifying
    @Query("delete from QuoteRequest qr where qr.client.id = :clientId")
    void deleteAllByClientId(@Param("clientId") Long clientId);
}

