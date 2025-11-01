package fr.mecanique.api.pmgl.pmgl_api.devis.controller;

import fr.mecanique.api.pmgl.pmgl_api.devis.bean.Devis;
import fr.mecanique.api.pmgl.pmgl_api.devis.bean.LigneDevis;
import fr.mecanique.api.pmgl.pmgl_api.devis.service.DevisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devis")
public class DevisController {

    private final DevisService devisService;

    @PostMapping("/create")
    public ResponseEntity<Devis> createDevis(
            @RequestParam Long clientId,
            @RequestParam Long adminId,
            @RequestParam(required = false) Long quoteRequestId,
            @RequestBody List<LigneDevis> lignes
    ) {
        return ResponseEntity.ok(devisService.createDevis(clientId, adminId, quoteRequestId, lignes));
    }

    @GetMapping("/all-devis")
    public ResponseEntity<Page<Devis>> allDevis(Pageable pageable)
    {
        return ResponseEntity.ok(this.devisService.all(pageable));
    }

    @DeleteMapping("/remove-devis-by-id/{devisId}")
    public ResponseEntity<Void> deleteDevis(@PathVariable Integer devisId) {
        devisService.deleteDevis(devisId);
        return ResponseEntity.noContent().build();
    }
}

