import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateQuoteRequestDTO } from '../model/create-quote-request.model';
import { Page, QuoteRequestDTO } from '../model/quote-request-dto.model';
import { GenericMethodeService } from '../../shared/generic-methode.service';

@Injectable({ providedIn: 'root' })
export class QuoteService {
  private apiUrl = 'http://localhost:9000/pmgl-api/quotes';

  constructor(
    private http: HttpClient,
    private generic: GenericMethodeService
  ) {}

  createQuoteRequest(quoteRequest: CreateQuoteRequestDTO): Observable<number> {
    const requestWithoutFiles = { ...quoteRequest, files: undefined };
    return this.http.post<number>(
      `${this.apiUrl}/quote-request`,
      requestWithoutFiles
    );
  }

  getAllQuoteRequests(page = 0, size = 10): Observable<Page<QuoteRequestDTO>> {
    return this.http.get<Page<QuoteRequestDTO>>(
      `${this.apiUrl}/quote-requests?page=${page}&size=${size}`,
      { headers: this.generic.getHeaders() }
    );
  }

  // à garder pour plus tard
  uploadFiles(quoteId: number, files: FormData) {
    return this.http.post<any>(`${this.apiUrl}/${quoteId}/files`, files, {
      reportProgress: true,
      observe: 'events',
      headers: this.generic.getHeadersSansContentType()
    });
  }
}
