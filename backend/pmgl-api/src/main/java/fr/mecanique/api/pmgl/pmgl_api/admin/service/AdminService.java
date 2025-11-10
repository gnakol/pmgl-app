package fr.mecanique.api.pmgl.pmgl_api.admin.service;

import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.account.enums.AccountType;
import fr.mecanique.api.pmgl.pmgl_api.account.repositorie.AccountRepository;
import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.AdminDTO;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.ConfirmInviteRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.InviteAdminRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.enums.AdminRole;
import fr.mecanique.api.pmgl.pmgl_api.admin.keycloak.KeycloakAdminService;
import fr.mecanique.api.pmgl.pmgl_api.admin.mappers.AdminMapper;
import fr.mecanique.api.pmgl.pmgl_api.admin.repositorie.AdminRepository;
import fr.mecanique.api.pmgl.pmgl_api.security.JwtService;
import fr.mecanique.api.pmgl.pmgl_api.uuid.service.UuidService;
import fr.mecanique.api.pmgl.pmgl_api.webservice.Webservice;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService implements Webservice<AdminDTO> {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final UuidService uuidService;

    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    private final KeycloakAdminService keycloakAdminService;
    private final AdminMapper adminMapper;


    public String inviteAdmin(InviteAdminRequest request) {
        // 1. Créer un compte désactivé
        Account account = Account.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())   // <-- requis en DB
                .name(request.getLastName())        // <-- requis en DB
                .civility(request.getCivility())
                .accountType(AccountType.admin)     // <-- ENUM, pas String
                .active(false)
                .referenceAccount(this.uuidService.generateUuid())
                .createdAt(LocalDateTime.now())
                .build();
        account = this.accountRepository.save(account);

        // 2. Générer le token d’invitation (role en minuscule)
        String token = this.jwtService.generateInvitationToken(Map.of(
                "email", account.getEmail(),
                "ref", account.getReferenceAccount(),
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "zone", request.getZone(),
                "civility", request.getCivility(),
                "role", "admin"   // <-- match l'ENUM DB
        ));

        // 3. URL d’inscription
        String link = "http://localhost:4200/admin-invite?token=" + token;

        // 4. Envoi mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getEmail());
        message.setSubject("🎟️ Invitation à rejoindre PMGL en tant qu’admin");
        message.setText(String.format("""
        Bonjour %s,

        Vous avez été invité à devenir administrateur chez PMGL.
        Cliquez sur ce lien pour finaliser votre inscription (valide 24h) :

        %s

        -- L'équipe PMGL.
        """, request.getFirstName(), link));
        mailSender.send(message);

        return "Invitation envoyée à " + account.getEmail();
    }


    public void confirmInvitation(ConfirmInviteRequest request) {
        Claims claims;
        try {
            claims = jwtService.validateInvitationToken(request.getToken());
        } catch (Exception e) {
            throw new RuntimeException("Token invalide ou expiré");
        }

        String email = claims.get("email", String.class);
        String ref = claims.get("ref", String.class);
        String firstName = claims.get("firstName", String.class);
        String lastName = claims.get("lastName", String.class);
        String zone = claims.get("zone", String.class);
        String civility = claims.get("civility", String.class);
        String role = claims.get("role", String.class);

        Account account = this.accountRepository.findByReferenceAccount(ref)
                .orElseThrow(() -> new RuntimeException("Aucun compte lié à cette invitation"));

        account.setActive(true);
        accountRepository.save(account);

        Admin admin = Admin.builder()
                .account(account)
                .zone(zone)
                .refAdmins(account.getReferenceAccount())
                .role(AdminRole.valueOf(role))
                .build();
        adminRepository.save(admin);

        this.keycloakAdminService.createAdminUser(email, request.getPassword());

        String keycloakId = this.keycloakAdminService.getUserIdByEmail(email);
        account.setKeycloakId(keycloakId);
        this.accountRepository.save(account);
    }

    public void deleteAdmin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable avec l'ID : " + adminId));

        // Récupérer le compte associé pour extraire l'email
        String email = admin.getAccount().getEmail();

        // Supprimer d'abord dans Keycloak
        keycloakAdminService.deleteAdminByEmail(email);

        // Supprimer l'admin + le compte en base
        adminRepository.delete(admin);
        accountRepository.delete(admin.getAccount());
    }
    @Transactional
    public Long getAdminIdByEmail(String email) {
        return adminRepository.findIdByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Aucun admin trouvé avec l'email : " + email));
    }

    @Transactional
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Aucun compte trouvé avec l'email : " + email));
    }

    @Override
    public Page<AdminDTO> all(Pageable pageable) {
        return null;
    }

    @Override
    public AdminDTO add(AdminDTO e) {
        return null;
    }

    @Override
    public AdminDTO update(Long id, AdminDTO e) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public Optional<AdminDTO> getById(Long id) {
        return this.adminRepository.findById(id)
                .map(this.adminMapper::fromAdmin);
    }


}
