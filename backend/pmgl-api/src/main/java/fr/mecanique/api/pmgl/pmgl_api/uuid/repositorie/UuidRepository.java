package fr.mecanique.api.pmgl.pmgl_api.uuid.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.uuid.bean.UuidPmgl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UuidRepository extends JpaRepository<UuidPmgl, Long> {

    boolean existsByUuidGenerate(String keyGenerate);
}