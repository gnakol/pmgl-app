import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { QuoteRequestComponent } from '../../quote-package/quote-request/quote-request.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LoginComponent } from '../../customer-package/authenticate/login/login.component';

@NgModule({
  declarations: [
    NavbarComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    QuoteRequestComponent,
    LoginComponent
  ],
  exports: [
    NavbarComponent
  ]
})
export class PageModule { }
