import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfirmInviteRequest } from '../../beans/confirm-invitation.model';
import { Observable } from 'rxjs';
import { InviteAdminRequest } from '../../beans/invite-admin-request.model';
import { AuthAdminService } from '../connect/auth-admin.service';
import { GenericMethodeService } from '../../../shared/generic-methode.service';
import { AccountDTO } from '../../../customer-package/beans/account-dto.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

   private apiUrlAdmin = 'http://localhost:9000/pmgl-api/admin';

  constructor(
    private http : HttpClient,
    private authAdmin : AuthAdminService,
    private headGenericMethode : GenericMethodeService
  ) { }

    confirmInvitation(data: ConfirmInviteRequest): Observable<string> {

    return this.http.post<string>(`${this.apiUrlAdmin}/confirm-invitation-admin`, data, {responseType : 'text' as 'json'});
  }

  inviteAdmin(data: InviteAdminRequest): Observable<string> {
    const token = localStorage.getItem('adminToken');
    if (!token) {
      throw new Error('Token admin manquant');
    }

    const headers = {
      Authorization: `Bearer ${token}`
    };

    return this.http.post<string>(`${this.apiUrlAdmin}/invite-admin`,data,
      { headers, responseType: 'text' as 'json' });
  }

  getAdminIdByEmail(email: string): Observable<number> {
    const token = this.authAdmin.getToken();
    if (!token) {
      throw new Error('Token admin manquant');
    }

    const headers = this.headGenericMethode.getHeaders();

    return this.http.get<number>(`${this.apiUrlAdmin}/getIdAdminByEmail`, {
      headers,
      params: { email }
    });
  }

  getAccountByEmail(email: string): Observable<AccountDTO> {
    const params = new HttpParams().set('email', email);
    return this.http.get<AccountDTO>(`${this.apiUrlAdmin}/get-account-by-email`, {
      headers: this.headGenericMethode.getHeaders(),
      params
    });
  }
}
