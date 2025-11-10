// src/app/component/devis-package/service/devis.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GenericMethodeService } from '../../shared/generic-methode.service';
import { QuoteLigneDTO } from '../beans/quote-lignes.model';
import { DevisDTO, PageResponse } from '../beans/quote-dto.model';

@Injectable({ providedIn: 'root' })
export class DevisService {
  private apiUrl = 'http://localhost:9000/pmgl-api/devis';

  constructor(
    private http: HttpClient,
    private generic: GenericMethodeService
  ) {}

  createDevis(
    clientId: number,
    adminId: number,
    quoteRequestId: number | null,
    lignes: QuoteLigneDTO[]
  ): Observable<any> {
    const url = `${this.apiUrl}/create?clientId=${clientId}&adminId=${adminId}${
      quoteRequestId ? `&quoteRequestId=${quoteRequestId}` : ''
    }`;
    return this.http.post<any>(url, lignes, { headers: this.generic.getHeaders() });
  }

  getAllDevis(page = 0, size = 20): Observable<PageResponse<DevisDTO>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<DevisDTO>>(
      `${this.apiUrl}/all-devis`,
      { headers: this.generic.getHeaders(), params }
    );
  }

  // ---- DELETE ----
  deleteDevis(devisId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/remove-devis-by-id/${devisId}`,
      { headers: this.generic.getHeaders() }
    );
  }
}
