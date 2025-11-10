import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';

export interface AuthDTO {
  email: string;
  password: string;
}

interface KeycloakToken {
  email?: string;
  realm_access?: { roles?: string[] };
  resource_access?: Record<string, { roles?: string[] }>;
}

@Injectable({ providedIn: 'root' })
export class LoginService {
  // ✅ endpoint exact
  private apiUrlLogin = 'http://localhost:9000/pmgl-api/authenticate/login';

  // état auth (navbar se met à jour)
  public authStatus = new BehaviorSubject<boolean>(this.isAuthenticated());

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(credentials: AuthDTO): Observable<string> {
    return this.http.post<{ token: string }>(this.apiUrlLogin, credentials).pipe(
      map((response) => {
        if (!response?.token) {
          throw new Error('Réponse invalide du serveur');
        }
        // ✅ clé unifiée
        localStorage.setItem('jwtToken', response.token);
        this.authStatus.next(true);
        return response.token;
      }),
      tap(() => {
        // ✅ redirige vers le dashboard client
        this.router.navigate(['/dashboard']);
      })
    );
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
    this.authStatus.next(false);
    this.router.navigate(['/']);
  }

  parseJwt(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (e) {
      console.error('Erreur lors du décodage du JWT :', e);
      return null;
    }
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
