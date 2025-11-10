package fr.mecanique.api.pmgl.pmgl_api.client.controller;

import fr.mecanique.api.pmgl.pmgl_api.client.dto.ConfirmInviteClientRequest;
import fr.mecanique.api.pmgl.pmgl_api.client.dto.CustomerDTO;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientInviteService;
import fr.mecanique.api.pmgl.pmgl_api.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/getIdClientByEmail")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV', 'ROLE_CUSTOMER')")
    public ResponseEntity<Long> getClientIdByEmail(@RequestParam String email) {
        Long clientId = clientService.getClientIdByEmail(email);
        return ResponseEntity.ok(clientId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV', 'ROLE_CUSTOMER')")
    @GetMapping("/all-customers")
    public ResponseEntity<Page<CustomerDTO>> allCustomer(Pageable pageable)
    {
        return ResponseEntity.ok(this.clientService.allCustomer(pageable));
    }
}

