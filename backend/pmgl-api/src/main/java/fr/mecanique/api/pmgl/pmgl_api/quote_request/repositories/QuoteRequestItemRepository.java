package fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories;


import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuoteRequestItemRepository extends JpaRepository<QuoteRequestItem, Integer> {
    Page<QuoteRequestItem> findByQuoteRequest_Id(Integer quoteRequestId);
}
