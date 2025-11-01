package fr.mecanique.api.pmgl.pmgl_api.quote_request.controllers;

import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.CreateQuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.QuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.services.QuoteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("quotes")
public class QuoteRequestController {

    private final QuoteRequestService service;

    @PostMapping("/quote-request")
    public ResponseEntity<Long> create(@Validated @RequestBody CreateQuoteRequestDTO dto) {
        Long id = service.createQuoteRequest(dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/quote-requests")
    public ResponseEntity<Page<QuoteRequestDTO>> getAllQuoteRequests(Pageable pageable) {
        Page<QuoteRequestDTO> quoteRequests = service.getAllQuoteRequests(pageable);
        return ResponseEntity.ok(quoteRequests);
    }

    @GetMapping("/quote-requests/client/{clientId}")
    public ResponseEntity<QuoteRequestDTO> getQuoteRequestByClientId(@PathVariable Long clientId) {
        QuoteRequestDTO quoteRequest = service.getQuoteRequestByClientId(clientId);
        return ResponseEntity.ok(quoteRequest);
    }
}

