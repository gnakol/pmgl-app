import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { QuoteRequestComponent } from '../../quote-package/quote-request/quote-request.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    NavbarComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    QuoteRequestComponent
  ],
  exports: [
    NavbarComponent
  ]
})
export class PageModule { }
