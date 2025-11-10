import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { LoginService } from '../customer-package/authenticate/core/login.service';

@Injectable({
  providedIn: 'root'
})
export class GenericMethodeService{

    constructor(
      private authCustomerService : LoginService

    ){}

    public getHeaders(): HttpHeaders {

        const token = this.authCustomerService.getToken();

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
          'Authorization': `Bearer ${this.authCustomerService.getToken()}`
        });
      }
      

}