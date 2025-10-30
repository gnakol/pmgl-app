package fr.mecanique.api.pmgl.pmgl_api.quote_request.controllers;

import fr.mecanique.api.pmgl.pmgl_api.quote_request.dto.CreateQuoteRequestDTO;
import fr.mecanique.api.pmgl.pmgl_api.quote_request.services.QuoteRequestService;
import lombok.RequiredArgsConstructor;
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
}

