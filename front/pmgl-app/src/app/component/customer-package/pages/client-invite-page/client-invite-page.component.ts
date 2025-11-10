import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfirmCreateAccountComponent } from '../../web-service/confirm-create-account/confirm-create-account.component';
ConfirmCreateAccountComponent

@Component({
  selector: 'app-client-invite-page',
  standalone: true,
  imports: [CommonModule, ConfirmCreateAccountComponent],
  template: `
    <app-confirm-create-account></app-confirm-create-account>
  `
})
export class ClientInvitePageComponent {}
