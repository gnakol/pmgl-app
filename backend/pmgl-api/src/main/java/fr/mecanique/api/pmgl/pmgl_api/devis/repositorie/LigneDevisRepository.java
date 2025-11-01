package fr.mecanique.api.pmgl.pmgl_api.devis.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.devis.bean.LigneDevis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneDevisRepository extends JpaRepository<LigneDevis, Integer> {
    List<LigneDevis> findByDevis_Id(Integer devisId);
}

