package fr.mecanique.api.pmgl.pmgl_api.admin.mail;

import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MailServiceAdmin {

    private final JavaMailSender mailSender;

    public void notifyStaffNewQuoteRequest(Long quoteRequestId, Client client, int itemsCount) {
        String subject = "🆕 Nouvelle demande de devis #" + quoteRequestId;
        String content = """
                Bonjour Michel,

                Une nouvelle demande de devis vient d'être créée.

                • ID demande: %d
                • Client: %s %s (%s)
                • Nombre de lignes: %d

                Notes:
                - Consulte le back-office pour les détails des pièces.
                - Statut initial: NOUVELLE

                — PMGL Bot
                """.formatted(
                quoteRequestId,
                client.getAccount().getFirstName(),
                client.getAccount().getName(),
                client.getAccount().getEmail(),
                itemsCount
        );

        sendEmail("inbox@pmgl.fr", subject, content);
    }

    public void notifyDevisCreated(Devis devis, int lignesCount) {
        String subject = "🧾 Devis " + devis.getNumeroDevis() + " créé avec succès";
        String content = """
            Bonjour Michel,

            Vous venez de créer un nouveau devis.

            • Numéro: %s
            • Client: %s %s (%s)
            • Nombre de lignes: %d
            • Montant HT: %.2f €
            • Statut: %s

            — PMGL Bot
            """.formatted(
                devis.getNumeroDevis(),
                devis.getClient().getAccount().getFirstName(),
                devis.getClient().getAccount().getName(),
                devis.getClient().getAccount().getEmail(),
                lignesCount,
                devis.getMontantHt() != null ? devis.getMontantHt() : BigDecimal.ZERO,
                devis.getStatut()
        );

        sendEmail("inbox@pmgl.fr", subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setText(content, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@pmgl.fr");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur d'envoi email (admin): " + e.getMessage());
        }
    }
}
