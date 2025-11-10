// quote-template.component.ts
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// ↓ tes listes
import { AllQuoteRequestComponent } from '../web-service/all-quote-request/all-quote-request.component';
import { AllDevisComponent } from '../../devis-package/web-service/all-devis/all-devis.component';

@Component({
  selector: 'app-quote-template',
  standalone: true,
  templateUrl: './quote-template.component.html',
  styleUrl: './quote-template.component.scss',
  imports: [
    CommonModule,
    HttpClientModule,
    AllQuoteRequestComponent,
    AllDevisComponent
  ]
})
export class QuoteTemplateComponent {
  // 'requests' = Demandes de devis, 'quotes' = Devis
  selectedView: 'requests' | 'quotes' = 'requests';

  setView(view: 'requests' | 'quotes') {
    this.selectedView = view;
  }
}
