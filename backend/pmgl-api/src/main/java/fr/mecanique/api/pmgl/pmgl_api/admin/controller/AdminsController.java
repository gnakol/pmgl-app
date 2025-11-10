package fr.mecanique.api.pmgl.pmgl_api.admin.controller;


import fr.mecanique.api.pmgl.pmgl_api.account.bean.Account;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.AdminDTO;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.ConfirmInviteRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.dto.InviteAdminRequest;
import fr.mecanique.api.pmgl.pmgl_api.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Slf4j
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

    @GetMapping("/getIdAdminByEmail")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV')")
    public ResponseEntity<Long> getAdminIdByEmail(@RequestParam String email) {
        Long adminId = adminService.getAdminIdByEmail(email);
        return ResponseEntity.ok(adminId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV')")
    @GetMapping("/get-admin-by-id/{idAdmin}")
    public ResponseEntity<AdminDTO> getByIdAdmin(@Validated @PathVariable Long idAdmin)
    {
         return  this.adminService.getById(idAdmin)
                .map(adminDTO -> {
                    log.info("Admin with Id : " +idAdmin+ " was found ");
                    return new ResponseEntity<>(adminDTO, HttpStatus.OK);
                })
                .orElseThrow(() -> {
                    log.error("Admin with ID : "+idAdmin+ " was not found");
                    throw new RuntimeException("Unable to retrieve Admin. Please check the provider ID");
                });
    }

    @GetMapping("/get-account-by-email")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER-ADMIN','ROLE_DEV')")
    public ResponseEntity<Account> getAccountByEmail(@RequestParam String email) {
        Account account = adminService.getAccountByEmail(email);
        return ResponseEntity.ok(account);
    }



}
