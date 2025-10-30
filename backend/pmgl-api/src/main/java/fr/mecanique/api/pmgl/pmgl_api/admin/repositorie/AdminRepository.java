package fr.mecanique.api.pmgl.pmgl_api.admin.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}
