// src/app/component/pages/navbar/navbar.component.ts
import { Component, OnDestroy, OnInit, HostListener, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription, combineLatest } from 'rxjs';
import { LoginService } from '../../customer-package/authenticate/core/login.service';
import { AuthAdminService } from '../../admin-package/security/connect/auth-admin.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  showQuoteModal = false;
  showLoginModal = false;

  /** états */
  isCustomer = false;
  isAdmin = false;
  isAnyAuth = false;

  /** profil/dropdown */
  showProfileMenu = false;
  profileName = 'Profil';
  dashboardRoute: string = '/';

  private sub?: Subscription;

  constructor(
    private loginService: LoginService,
    private adminAuth: AuthAdminService,
    private router: Router,
    private host: ElementRef
  ) {}

  ngOnInit(): void {
    // combineLatest pour suivre les 2 statuts
    this.sub = combineLatest([
      this.loginService.authStatus,        // client
      this.adminAuth.adminStatus           // admin
    ]).subscribe(([cust, adm]) => {
      this.isCustomer = cust;
      this.isAdmin = adm && this.adminAuth.isAdmin(); // par sécurité
      this.isAnyAuth = this.isCustomer || this.isAdmin;

      // Nom affiché
      if (this.isAdmin) {
        this.profileName = this.adminAuth.getEmailFromToken() ?? 'Admin';
        this.dashboardRoute = '/dashboard-admin';
      } else if (this.isCustomer) {
        const t = this.loginService.getToken();
        // tu peux décoder le nom si tu l'as mis dans le JWT côté client
        this.profileName = 'Mon espace';
        this.dashboardRoute = '/dashboard-customer';
      } else {
        this.profileName = 'Profil';
        this.dashboardRoute = '/';
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  // Fermer le dropdown si clic à l'extérieur
  @HostListener('document:click', ['$event'])
  onDocClick(ev: MouseEvent) {
    if (!this.host.nativeElement.contains(ev.target)) {
      this.showProfileMenu = false;
    }
  }

  /* actions UI */
  openQuoteModal() { this.showQuoteModal = true; }
  closeQuoteModal() { this.showQuoteModal = false; }

  goLogin(): void { this.showLoginModal = true; }
  goJoin(): void { this.router.navigate(['/login']); } // remplacera par /register plus tard

  navigateToHome() { this.router.navigate(['/']); }

  // Profil dropdown actions
  goDashboard(): void {
    this.router.navigate([this.dashboardRoute]);
    this.showProfileMenu = false;
  }
  goNotifications(): void {
    // à brancher plus tard
    this.showProfileMenu = false;
  }
  goSettings(): void {
    // à brancher plus tard
    this.showProfileMenu = false;
  }

  logout(): void {
    // si admin connecté -> logout admin ; si client connecté -> logout client ; (si les 2, on vide tout)
    if (this.isAdmin) this.adminAuth.logout();
    if (this.isCustomer) this.loginService.logout();
    this.showProfileMenu = false;
    this.router.navigate(['/']);
  }
}