package fr.mecanique.api.pmgl.pmgl_api.devis.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DevisRepository extends JpaRepository<Devis, Integer> {
    Optional<Devis> findByNumeroDevis(String numero);
    Page<Devis> findAll(Pageable pageable);

}

