// src/app/component/admin-package/security/core/auth-admin.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

interface KeycloakToken {
  email?: string;
  realm_access?: { roles?: string[] };
  resource_access?: Record<string, { roles?: string[] }>;
}

@Injectable({ providedIn: 'root' })
export class AuthAdminService {
  private apiUrlLogin = 'http://localhost:9000/pmgl-api/authenticate/login';

  /** ← NEW */
  public adminStatus = new BehaviorSubject<boolean>(!!localStorage.getItem('adminToken'));

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<string> {
    return this.http.post<{ token: string }>(this.apiUrlLogin, { email, password }).pipe(
      map((response) => {
        if (response?.token) {
          localStorage.setItem('adminToken', response.token);
          this.adminStatus.next(true);                 // ← NEW
          window.dispatchEvent(new Event('admin-logged-in'));
          return response.token;
        }
        throw new Error('Token non reçu');
      })
    );
  }

  logout(): void {
    localStorage.removeItem('adminToken');
    this.adminStatus.next(false);                      // ← NEW
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem('adminToken');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private getRoles(): string[] {
    const token = this.getToken();
    if (!token) return [];
    try {
      const decoded = jwtDecode<KeycloakToken>(token);
      const realmRoles = decoded?.realm_access?.roles ?? [];
      const resourceRoles = Object.values(decoded?.resource_access ?? {}).flatMap(r => r.roles ?? []);
      return [...realmRoles, ...resourceRoles];
    } catch {
      return [];
    }
  }

  isAdmin(): boolean {
    const roles = this.getRoles();
    return roles.includes('ADMIN') || roles.includes('SUPER-ADMIN');
  }

  getEmailFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded = jwtDecode<KeycloakToken>(token);
      return decoded?.email ?? null;
    } catch {
      return null;
    }
  }
}
