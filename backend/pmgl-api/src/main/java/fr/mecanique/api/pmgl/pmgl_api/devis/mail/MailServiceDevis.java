package fr.mecanique.api.pmgl.pmgl_api.devis.mail;

import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.LigneDevis;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailServiceDevis {

    private final JavaMailSender mailSender;

    /** ✉️ Mail au client quand le devis est prêt (avec résumé + CTA) */
    public void notifyDevisReady(String toEmail, String firstName, Devis devis, List<LigneDevis> lignes, String inviteLink) {
        String subject = "🧾 Votre devis " + devis.getNumeroDevis() + " est disponible";

        // On résume 2 premières lignes max
        StringBuilder linesPreview = new StringBuilder();
        int max = Math.min(lignes != null ? lignes.size() : 0, 2);
        for (int i = 0; i < max; i++) {
            LigneDevis l = lignes.get(i);
            BigDecimal totalLigne = l.getPrixUnitaireHt().multiply(BigDecimal.valueOf(l.getQuantite()));
            linesPreview.append("""
                <tr>
                  <td style="padding:6px 8px;border-bottom:1px solid #eee;">%s</td>
                  <td style="padding:6px 8px;border-bottom:1px solid #eee;text-align:center;">%d</td>
                  <td style="padding:6px 8px;border-bottom:1px solid #eee;text-align:right;">%.2f €</td>
                  <td style="padding:6px 8px;border-bottom:1px solid #eee;text-align:right;">%.2f €</td>
                </tr>
                """.formatted(
                    l.getDescriptionLigne(),
                    l.getQuantite(),
                    l.getPrixUnitaireHt(),
                    totalLigne
            ));
        }
        if ((lignes != null ? lignes.size() : 0) > 2) {
            linesPreview.append("""
                <tr>
                  <td colspan="4" style="padding:8px;color:#666;">… et %d autres ligne(s)</td>
                </tr>
                """.formatted(lignes.size() - 2));
        }

        String html = """
        <div style="font-family:Inter,Arial,sans-serif;font-size:15px;color:#111;line-height:1.5">
          <p>Bonjour %s,</p>
          <p>Votre devis <strong>%s</strong> est prêt.</p>

          <table style="border-collapse:collapse;width:100%%;margin:12px 0">
            <tr>
              <td style="padding:6px 0;color:#666">Validité</td>
              <td style="padding:6px 0;text-align:right"><strong>%s</strong></td>
            </tr>
            <tr>
              <td style="padding:6px 0;color:#666">Montant HT</td>
              <td style="padding:6px 0;text-align:right"><strong>%.2f €</strong></td>
            </tr>
            <tr>
              <td style="padding:6px 0;color:#666">TVA</td>
              <td style="padding:6px 0;text-align:right"><strong>%.2f %%</strong></td>
            </tr>
          </table>

          <div style="margin:16px 0 8px 0;font-weight:600">Aperçu des lignes</div>
          <table style="border-collapse:collapse;width:100%%;border:1px solid #eee">
            <thead>
              <tr style="background:#fafafa">
                <th style="padding:8px;text-align:left;border-bottom:1px solid #eee">Description</th>
                <th style="padding:8px;text-align:center;border-bottom:1px solid #eee">Qté</th>
                <th style="padding:8px;text-align:right;border-bottom:1px solid #eee">PU HT</th>
                <th style="padding:8px;text-align:right;border-bottom:1px solid #eee">Total</th>
              </tr>
            </thead>
            <tbody>
              %s
            </tbody>
          </table>

          <p style="margin:18px 0 8px 0">
            Pour consulter le devis complet (PDF) et si tout est OK, finaliser votre compte pour signer et poursuivre :
          </p>

          <div style="margin:18px 0">
            <a href="%s"
               style="display:inline-block;padding:12px 18px;background:#111;color:#fff;text-decoration:none;border-radius:8px;font-weight:600">
              Accéder à mon espace & valider
            </a>
          </div>

          <p style="color:#666;margin-top:14px">
            ⚠️ Le lien est valide 24h. Si besoin, vous pourrez en demander un nouveau.
          </p>

          <p>À très vite,<br/>— L'équipe PMGL</p>
        </div>
        """.formatted(
                firstName != null ? firstName : "",
                devis.getNumeroDevis(),
                devis.getDateValidite() != null ? devis.getDateValidite() : "—",
                devis.getMontantHt() != null ? devis.getMontantHt() : BigDecimal.ZERO,
                devis.getTva() != null ? devis.getTva() : new BigDecimal("20.00"),
                linesPreview.toString(),
                inviteLink
        );

        sendHtml(toEmail, subject, html);
    }

    public void notifyAccountActivated(String toEmail, String firstName) {
        String subject = "🎉 Votre compte PMGL a été créé avec succès !";
        String html = """
        <div style="font-family:Inter,Arial,sans-serif;font-size:15px;color:#111;line-height:1.6">
          <p>Bonjour %s,</p>
          <p>Bonne nouvelle 🎊 votre compte client PMGL vient d’être activé !</p>
          <p>Vous pouvez désormais vous connecter à votre espace client pour :</p>
          <ul style="margin:10px 0 14px 20px">
            <li>Consulter vos devis et leurs statuts</li>
            <li>Télécharger vos documents</li>
            <li>Échanger avec notre équipe</li>
          </ul>
          <p style="margin-top:18px">
            <a href="https://pmgl.fr/login"
               style="display:inline-block;background:#111;color:#fff;padding:12px 18px;border-radius:8px;text-decoration:none;font-weight:600">
               Accéder à mon compte
            </a>
          </p>
          <p style="margin-top:18px;color:#666">
            À très bientôt 👋<br/>— L’équipe PMGL
          </p>
        </div>
    """.formatted(firstName != null ? firstName : "");

        sendHtml(toEmail, subject, html);
    }


    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "utf-8");
            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@pmgl.fr");
            // 👉 plus tard: helper.addAttachment("devis.pdf", ...);
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur d'envoi email (HTML) : " + e.getMessage());
        }
    }
}
