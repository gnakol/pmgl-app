// src/app/component/customer-package/web-service/all-customer/all-customer.component.ts
import { Component, OnInit } from '@angular/core';
import { CustomerWithAccount } from '../../beans/customer-dto.model';
import { CustomerService } from '../../service/customer.service';
import { LoginService } from '../../authenticate/core/login.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-all-customer',
  templateUrl: './all-customer.component.html',
  styleUrls: ['./all-customer.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AllCustomerComponent implements OnInit {
  customers: CustomerWithAccount[] = [];
  loading = false;
  error = '';

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;

  constructor(
    private customerService: CustomerService,
    private loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.error = '';

    this.customerService.getAllCustomers(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.customers = response.content.map((c) => this.customerService.generateStaticData(c));
        this.totalElements = response.totalElements;
        this.loading = false;
        this.loadAccountsInfo();
      },
      error: (err) => {
        console.error('Erreur lors du chargement des clients:', err);
        this.error = 'Erreur lors du chargement des clients';
        this.loading = false;
      }
    });
  }

  private loadAccountsInfo(): void {
    this.customers.forEach((customer, index) => {
      if (customer.accountId) {
        this.customerService.getAccountById(customer.accountId).subscribe({
          next: (account) => (this.customers[index].account = account),
          error: (err) => console.error(`Erreur lors du chargement du account ${customer.accountId}:`, err)
        });
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadCustomers();
  }

  // UI helpers
  getInitials(c: CustomerWithAccount): string {
    if (c.account) {
      const f = c.account.firstName?.[0] ?? '';
      const l = c.account.lastName?.[0] ?? '';
      return `${f}${l}`.toUpperCase() || 'CL';
    }
    return c.raisonSociale ? c.raisonSociale.substring(0, 2).toUpperCase() : 'CL';
  }

  getDisplayName(c: CustomerWithAccount): string {
    if (c.account && (c.account.firstName || c.account.lastName)) {
      return `${c.account.firstName ?? ''} ${c.account.lastName ?? ''}`.trim();
    }
    return c.raisonSociale || 'Client';
  }

  getClientType(c: CustomerWithAccount): string {
    switch (c.typeClient) {
      case 'ENTREPRISE': return 'Entreprise';
      case 'PARTICULIER': return 'Particulier';
      case 'STARTUP':     return 'Startup';
      default:            return 'Client';
    }
  }

  getSector(_: CustomerWithAccount): string {
    const sectors = ['Aéronautique', 'Médical', 'Automobile', 'Agroalimentaire'];
    return sectors[Math.floor(Math.random() * sectors.length)];
  }

  getAvatarClasses(c: CustomerWithAccount): string {
    const base = 'w-16 h-16 rounded-xl flex items-center justify-center';
    switch (c.typeClient) {
      case 'ENTREPRISE': return `${base} bg-gradient-to-r from-blue-500 to-blue-600`;
      case 'PARTICULIER':return `${base} bg-gradient-to-r from-green-500 to-green-600`;
      case 'STARTUP':    return `${base} bg-gradient-to-r from-purple-500 to-purple-600`;
      default:           return `${base} bg-gradient-to-r from-gray-500 to-gray-600`;
    }
  }
  getTypeBadgeClasses(c: CustomerWithAccount): string {
    switch (c.typeClient) {
      case 'ENTREPRISE': return 'bg-blue-100 text-blue-800';
      case 'PARTICULIER':return 'bg-green-100 text-green-800';
      case 'STARTUP':    return 'bg-purple-100 text-purple-800';
      default:           return 'bg-gray-100 text-gray-800';
    }
  }
  getFidelityBadgeClasses(c: CustomerWithAccount): string {
    switch (c.statutFidelite) {
      case 'Fidèle':      return 'bg-green-100 text-green-800';
      case 'Nouveau':     return 'bg-blue-100 text-blue-800';
      case 'Occasionnel': return 'bg-orange-100 text-orange-800';
      default:            return 'bg-gray-100 text-gray-800';
    }
  }
  getTotalPages(): number {
    return Math.ceil(this.totalElements / this.pageSize);
  }
}
