import { Component } from '@angular/core';
import { AllCustomerComponent } from '../../customer-package/web-service/all-customer/all-customer.component';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.scss',
  imports : [
    AllCustomerComponent
  ]
})
export class CustomerDashboardComponent {

}
