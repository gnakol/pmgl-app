package fr.mecanique.api.pmgl.pmgl_api.quote_request.services;

import fr.mecanique.api.pmgl.pmgl_api.admin.mail.MailServiceAdmin;
import fr.mecanique.api.pmgl.pmgl_api.client.bean.Client;
import fr.mecanique.api.pmgl.pmgl_api.client.repositorie.ClientRepository;
import fr.mecanique.api.pmgl.pmgl_api.client.mail.MailServiceClient;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.bean.QuoteRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteRequestMailListener {

    private final MailServiceClient mailServiceClient;
    private final MailServiceAdmin mailServiceAdmin;
    private final ClientRepository clientRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(QuoteRequestCreatedEvent ev) {
        try {
            // mail client si email présent
            if (StringUtils.hasText(ev.getClientEmail())) {
                mailServiceClient.notifyQuoteRequestReceived(ev.getClientEmail(), ev.getClientFirstName());
            } else {
                log.warn("QuoteRequest {}: pas d'email client, skip notification.", ev.getQuoteRequestId());
            }

            // mail interne (Michel) — on charge le Client par son id
            Client client = clientRepository.findById(ev.getClientId()).orElse(null);
            if (client != null) {
                mailServiceAdmin.notifyStaffNewQuoteRequest(ev.getQuoteRequestId(), client, ev.getItemsCount());
            } else {
                log.warn("QuoteRequest {}: client {} introuvable, skip mail admin.",
                        ev.getQuoteRequestId(), ev.getClientId());
            }
        } catch (Exception e) {
            log.error("Erreur envoi mails pour QuoteRequest {}: {}", ev.getQuoteRequestId(), e.getMessage(), e);
        }
    }
}
