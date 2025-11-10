import { CanActivate, CanActivateFn, Router, UrlTree } from '@angular/router';
import { LoginService } from '../core/login.service';
import { Observable } from 'rxjs';

export class CustomerGuard implements CanActivate {
  
  constructor(
    private authCustomer : LoginService,
    private router : Router
  ){}

  canActivate(): boolean | UrlTree {
    return this.authCustomer.isAuthenticated() ? true : this.router.parseUrl('/login');
  }

  }

