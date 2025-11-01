package fr.mecanique.api.pmgl.pmgl_api.client.controller;

import fr.mecanique.api.pmgl.pmgl_api.client.dto.ConfirmInviteClientRequest;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientInviteService;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientInviteService clientInviteService;

    @DeleteMapping("/remove-client-local/{id}")
    public ResponseEntity<Void> deleteCompletely(@PathVariable("id") Long id) {
        clientService.deleteClientCompletely(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm-invite-client")
    public ResponseEntity<String> confirmInvite(@RequestBody ConfirmInviteClientRequest request) {
        this.clientInviteService.confirmInvitation(request);
        return ResponseEntity.ok("Compte client activé avec succès.");
    }
}

