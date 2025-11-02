import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateQuoteRequestDTO } from '../model/create-quote-request.model';

@Injectable({
  providedIn: 'root'
})
export class QuoteService {
  private apiUrl = 'http://localhost:9000/pmgl-api/quotes';

  constructor(private http: HttpClient) { }

  createQuoteRequest(quoteRequest: CreateQuoteRequestDTO): Observable<number> {
    // Pour l'instant on envoie sans fichiers, on les gérera plus tard
    const requestWithoutFiles = {
      ...quoteRequest,
      files: undefined // On retire les fichiers pour l'instant
    };
    
    return this.http.post<number>(`${this.apiUrl}/quote-request`, requestWithoutFiles);
  }

  // Méthode pour uploader les fichiers séparément (à implémenter plus tard)
  uploadFiles(quoteId: number, files: FormData): Observable<HttpEvent<any>> {
    return this.http.post<any>(`${this.apiUrl}/${quoteId}/files`, files, {
      reportProgress: true,
      observe: 'events'
    });
  }
}