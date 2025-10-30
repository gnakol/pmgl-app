package fr.mecanique.api.pmgl.pmgl_api.admin.controller;


import fr.mecanique.api.pmgl.pmgl_api.admin.dto.ConfirmInviteRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.InviteAdminRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminsController {

    private final AdminService adminService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV')")
    @PostMapping("/invite-admin")
    public ResponseEntity<String> inviteAdmin(@RequestBody InviteAdminRequest request) {
        String message = adminService.inviteAdmin(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/confirm-invitation-admin")
    public ResponseEntity<String> confirmInvitation(@RequestBody ConfirmInviteRequest request) {
        this.adminService.confirmInvitation(request);
        return ResponseEntity.ok("Inscription administrateur confirmée !");
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPER-ADMIN','ROLE_DEV')")
    @DeleteMapping("/remove-top-admin/{adminId}")
    public ResponseEntity<Void> deleteAdmin(@Validated @PathVariable Long adminId) {
        this.adminService.deleteAdmin(adminId);
        return ResponseEntity.noContent().build();
    }

}
