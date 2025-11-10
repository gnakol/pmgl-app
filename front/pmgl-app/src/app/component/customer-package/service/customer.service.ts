// src/app/component/customer-package/service/customer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { CustomerDTO, CustomerWithAccount } from '../beans/customer-dto.model';
import { AccountDTO } from '../beans/account-dto.model';
import { GenericMethodeService } from '../../shared/generic-methode.service';

export interface ConfirmInviteRequest {
  token: string;
  password: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private apiUrl = 'http://localhost:9000/pmgl-api/client';
  private accountApiUrl = 'http://localhost:9000/pmgl-api/account';

  constructor(
    private http: HttpClient,
    private genericMethodService: GenericMethodeService
  ) {}

  confirmInvitation(request: ConfirmInviteRequest): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/confirm-invite-client`,
      request,
      { responseType: 'text' as 'json' } // <- évite le "200 / ok:false"
    );
  }

  getAllCustomers(page = 0, size = 10): Observable<PageResponse<CustomerDTO>> {
    const headers = this.genericMethodService.getHeaders();
    return this.http.get<PageResponse<CustomerDTO>>(
      `${this.apiUrl}/all-customers?page=${page}&size=${size}`,
      { headers }
    );
  }

  /** ---- NORMALISATION ---- */
  private normalizeAccount(api: any): AccountDTO {
    return {
      id: api?.id ?? api?.id_account ?? api?.accountId,
      // back peut renvoyer firstName ou first_name
      firstName: api?.firstName ?? api?.first_name ?? '',
      // back utilise souvent "name" pour le nom de famille
      lastName: api?.lastName ?? api?.name ?? api?.last_name ?? '',
      email: api?.email ?? '',
      civility: api?.civility ?? 'MR',
    };
  }

  // Récupérer l'account par ID (pour la liste admin)
  getAccountById(accountId: number): Observable<AccountDTO> {
    const headers = this.genericMethodService.getHeaders();
    return this.http
      .get<any>(`${this.accountApiUrl}/get-account-by-id/${accountId}`, { headers })
      .pipe(map((api) => this.normalizeAccount(api)));
  }

  // (Optionnel) Récupérer l'account par email si tu as l’endpoint côté back
  getAccountByEmail(email: string): Observable<AccountDTO> {
    const headers = this.genericMethodService.getHeaders();
    return this.http
      .get<any>(`${this.accountApiUrl}/get-account-by-email?email=${encodeURIComponent(email)}`, { headers })
      .pipe(map((api) => this.normalizeAccount(api)));
  }

  // Données statiques temporaires
// customer.service.ts
generateStaticData(customer: CustomerDTO, account?: AccountDTO): CustomerWithAccount {
  const seed = (customer.id ?? customer.accountId ?? 0);

  // Simple deterministic helpers
  const sectors = ['Aéronautique', 'Médical', 'Automobile', 'Agroalimentaire'];
  const statutPool: Array<'Fidèle' | 'Nouveau' | 'Occasionnel'> = ['Fidèle', 'Nouveau', 'Occasionnel'];

  const projetsActifs = (seed % 5) + 1;                                   // 1..5
  const caAnnuel = ((seed * 48271) % 50000) + 1000;                        // 1k..50k
  const statutFidelite = statutPool[seed % statutPool.length];
  const sector = sectors[seed % sectors.length];

  return {
    ...customer,
    account,
    projetsActifs,
    caAnnuel,
    statutFidelite,
    sector
  };
}


}
