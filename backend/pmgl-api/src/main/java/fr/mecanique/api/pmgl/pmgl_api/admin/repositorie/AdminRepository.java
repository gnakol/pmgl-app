package fr.mecanique.api.pmgl.pmgl_api.admin.repositorie;

import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a.idAdmin FROM Admin a WHERE a.account.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}