import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { AuthAdminService } from '../admin-package/security/connect/auth-admin.service';

@Injectable({
  providedIn: 'root'
})
export class GenericMethodeService{

    constructor(private authService : AuthAdminService){}

    public getHeaders(): HttpHeaders {

        const token = this.authService.getToken();

        if (!token) {
          throw new Error('No token found');
        }
        return new HttpHeaders({
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        });
      }

      getHeadersSansContentType() {
        return new HttpHeaders({
          'Authorization': `Bearer ${this.authService.getToken()}`
        });
      }
      

}