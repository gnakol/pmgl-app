package fr.mecanique.api.pmgl.pmgl_api.quote_request.bean;

import lombok.Value;

@Value
public class QuoteRequestCreatedEvent {
    Long quoteRequestId;
    Long clientId;
    String clientEmail;
    String clientFirstName;
    int itemsCount;
}

