package fr.mecanique.api.pmgl.pmgl_api.devis.service;

import fr.mecanique.api.pmgl.pmgl_api.admin.bean.Admin;
import fr.mecanique.api.pmgl.pmgl_api.admin.mail.MailServiceAdmin;
import fr.mecanique.api.pmgl.pmgl_api.admin.repositorie.AdminRepository;
import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.repositorie.ClientRepository;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.LigneDevis;
import fr.mecanique.api.pmgl.pmgl_api.devis.enums.DevisStatut;
import fr.mecanique.api.pmgl.pmgl_api.devis.mail.MailServiceDevis;
import fr.mecanique.api.pmgl.pmgl_api.devis.repositorie.DevisRepository;
import fr.mecanique.api.pmgl.pmgl_api.devis.repositorie.LigneDevisRepository;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequest;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.repositories.QuoteRequestRepository;
import fr.mecanique.api.pmgl.pmgl_api.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DevisService {

    private final DevisRepository devisRepository;
    private final LigneDevisRepository ligneDevisRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final QuoteRequestRepository quoteRequestRepository;

    private final
    MailServiceDevis mailServiceDevis;
    private final MailServiceAdmin mailServiceAdmin;
    private final JwtService jwtService;

    @Value("${app.front-url}")
    private String frontUrl;

    @Transactional
    public Devis createDevis(Long clientId, Long adminId, Long quoteRequestId, List<LigneDevis> lignes) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable: " + adminId));

        QuoteRequest req = null;
        if (quoteRequestId != null) {
            req = quoteRequestRepository.findById(quoteRequestId)
                    .orElseThrow(() -> new EntityNotFoundException("QuoteRequest introuvable: " + quoteRequestId));
        }

        Devis devis = Devis.builder()
                .client(client)
                .admin(admin)
                .quoteRequest(req)
                .numeroDevis(generateNumeroDevis())
                .statut(DevisStatut.BROUILLON)
                .dateCreation(LocalDateTime.now())
                .dateValidite(LocalDate.now().plusDays(30))
                .tva(new BigDecimal("20.00"))
                .build();

        Devis saved = devisRepository.save(devis);

        BigDecimal totalHt = BigDecimal.ZERO;
        int count = 0;
        if (lignes != null) {
            for (LigneDevis l : lignes) {
                l.setDevis(saved);
                ligneDevisRepository.save(l);
                totalHt = totalHt.add(l.getPrixUnitaireHt().multiply(BigDecimal.valueOf(l.getQuantite())));
                count++;
            }
        }
        saved.setMontantHt(totalHt);

        // 👉 On considère l’envoi comme “fait” à ce moment (sinon garde BROUILLON si tu veux un bouton “Envoyer”)
        saved.setStatut(DevisStatut.ENVOYE);
        saved.setDateEnvoi(LocalDateTime.now());

        saved = devisRepository.save(saved);

        // ===== Générer le lien d’invitation client (valide 24h) =====
        String token = jwtService.generateInvitationToken(Map.of(
                "email", client.getAccount().getEmail(),
                "ref", client.getAccount().getReferenceAccount(),
                "clientId", client.getId(),
                "role", "CUSTOMER",
                "devis", saved.getNumeroDevis()
        ));
        String inviteLink = frontUrl + "/client-invite?token=" + token;

        // ===== Envoyer les mails =====
        try {
            mailServiceDevis.notifyDevisReady(
                    client.getAccount().getEmail(),
                    client.getAccount().getFirstName(),
                    saved,
                    lignes != null ? lignes : Collections.emptyList(),
                    inviteLink
            );
            System.out.println("✅ Mail client envoyé à: " + client.getAccount().getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi mail client: " + e.getMessage());
            e.printStackTrace();
        }

        // Décommente et corrige cette ligne :
        try {
            mailServiceAdmin.notifyDevisCreated(saved, count);
            System.out.println("✅ Mail admin envoyé");
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi mail admin: " + e.getMessage());
            e.printStackTrace();
        }

        return saved;
    }

    private String generateNumeroDevis() {
        return "DEV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Page<Devis> all(Pageable pageable) {
        return this.devisRepository.findAll(pageable);
    }

    @Transactional
    public void deleteDevis(Integer devisId) {
        Devis devis = devisRepository.findById(devisId)
                .orElseThrow(() -> new EntityNotFoundException("Devis introuvable: " + devisId));

        // Suppression en cascade des lignes de devis grâce au orphanRemoval = true
        devisRepository.delete(devis);
    }
}
