import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateQuoteRequestDTO } from '../model/create-quote-request.model';

@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private apiUrl = 'http://localhost:9000/pmgl-api/quotes';

  constructor(private http: HttpClient) { }

  createQuoteRequest(quoteRequest: CreateQuoteRequestDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/quote-request`, quoteRequest);
  }
}