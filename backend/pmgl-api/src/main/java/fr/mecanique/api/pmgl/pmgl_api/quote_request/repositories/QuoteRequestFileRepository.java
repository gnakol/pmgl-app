package fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories;


import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRequestFileRepository extends JpaRepository<QuoteRequestFile, Long> {
    List<QuoteRequestFile> findByQuoteRequest_Id(Long quoteRequestId);
}

