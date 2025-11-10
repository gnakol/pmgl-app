import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginAdminComponent } from '../login-admin/login-admin.component';

@Component({
  selector: 'app-admin-login-page',
  standalone: true,
  imports: [
    LoginAdminComponent
  ],
  template: `
    <app-login-admin (closed)="onClosed()"></app-login-admin>
  `
})
export class AdminLoginPage {
  constructor(private router: Router) {}
  onClosed() { this.router.navigateByUrl('/'); }
}
