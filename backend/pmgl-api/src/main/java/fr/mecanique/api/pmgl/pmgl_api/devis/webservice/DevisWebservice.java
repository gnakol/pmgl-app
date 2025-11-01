package fr.mecanique.api.pmgl.pmgl_api.devis.webservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DevisWebservice <T> {

    Page<T> all(Pageable pageable);
}
