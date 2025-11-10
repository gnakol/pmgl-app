package fr.mecanique.api.pmgl.pmgl_api.client.webservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerWebservice<T>{

    Page<T> allCustomer(Pageable pageable);
}
