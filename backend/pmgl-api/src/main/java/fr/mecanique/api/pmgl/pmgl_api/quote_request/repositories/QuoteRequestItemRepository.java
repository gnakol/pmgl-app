package fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories;


import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QuoteRequestItemRepository extends JpaRepository<QuoteRequestItem, Integer> {
    List<QuoteRequestItem> findByQuoteRequest_Id(Integer quoteRequestId);
}
