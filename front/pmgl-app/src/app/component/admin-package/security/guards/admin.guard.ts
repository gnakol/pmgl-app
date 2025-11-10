import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthAdminService } from '../connect/auth-admin.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private auth: AuthAdminService, private router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.auth.isAuthenticated() && this.auth.isAdmin()) {
      return true;
    }
    // Redirige vers la page d'accueil avec un param pour ouvrir la modale admin si tu veux
    return this.router.parseUrl('/admin-login');
  }
}
