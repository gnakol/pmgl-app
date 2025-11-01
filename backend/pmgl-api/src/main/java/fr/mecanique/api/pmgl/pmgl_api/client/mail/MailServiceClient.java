package fr.mecanique.api.pmgl.pmgl_api.client.mail;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailServiceClient {

    private final JavaMailSender mailSender;

    public void notifyQuoteRequestReceived(String toEmail, String firstName) {
        String subject = "📩 PMGL — Nous avons bien reçu votre demande de devis";
        String content = """
                Bonjour %s,

                Merci pour votre demande de devis chez PMGL — elle vient d'être enregistrée et transmise à notre équipe.
                
                ▶️ Prochaine étape
                Un administrateur va analyser les éléments (pièces, matières, quantités, délais) et vous reviendra rapidement.

                À très vite,
                — L'équipe PMGL
                """.formatted(firstName != null ? firstName : "");

        sendText(toEmail, subject, content);
    }

    private void sendText(String to, String subject, String content) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "utf-8");
            helper.setText(content, false);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@pmgl.fr");
            mailSender.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur d'envoi email (TXT) : " + e.getMessage());
        }
    }

}


