package fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories;


import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRequestFileRepository extends JpaRepository<QuoteRequestFile, Long> { }

