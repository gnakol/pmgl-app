package fr.mecanique.api.pmgl.pmgl_api.client.controller;

import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @DeleteMapping("/remove-client-local/{id}")
    public ResponseEntity<Void> deleteCompletely(@PathVariable("id") Long id) {
        clientService.deleteClientCompletely(id);
        return ResponseEntity.noContent().build();
    }
}

