import { Component, OnInit } from '@angular/core';
import { DevisDTO, PageResponse } from '../../beans/quote-dto.model';
import { AccountDTO } from '../../../customer-package/beans/account-dto.model';
import { DevisService } from '../../service/devis.service';
import { AuthAdminService } from '../../../admin-package/security/connect/auth-admin.service';
import { AdminService } from '../../../admin-package/security/service/admin.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-all-devis',
  standalone: true,
  templateUrl: './all-devis.component.html',
  styleUrls: ['./all-devis.component.scss'],
  imports : [ CommonModule, FormsModule, ReactiveFormsModule ]
})
export class AllDevisComponent implements OnInit {
  page = 0;
  size = 20;

  loading = false;
  error?: string;

  data?: PageResponse<DevisDTO>;
  adminAccount?: AccountDTO | null;

  // Stats
  enAttente = 0;
  enCours = 0;
  acceptes = 0;
  caTotal = 0;

  // ids en cours de suppression (désactive bouton + montre spinner)
  deletingIds = new Set<number>();

  constructor(
    private devisService: DevisService,
    private authAdmin: AuthAdminService,
    private adminService: AdminService
  ) {}

  ngOnInit(): void {
    this.fetchAdminAccount();
    this.loadPage(0);
  }

  private fetchAdminAccount(): void {
    const email = this.authAdmin.getEmailFromToken();
    if (!email) { this.adminAccount = null; return; }
    this.adminService.getAccountByEmail(email).subscribe({
      next: acc => this.adminAccount = acc,
      error: _ => this.adminAccount = null
    });
  }

  loadPage(p: number): void {
    this.loading = true;
    this.error = undefined;
    this.devisService.getAllDevis(p, this.size).subscribe({
      next: (res) => {
        this.data = res;
        this.page = res.number;
        this.size = res.size;
        this.computeStats(res.content);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Erreur de chargement des devis';
        this.loading = false;
      }
    });
  }

  private computeStats(list: DevisDTO[]): void {
    const U = (s: string|undefined|null) => (s ?? '').toUpperCase();
    this.enAttente = list.filter(d => ['BROUILLON','ENVOYE'].includes(U(d.statut))).length;
    this.enCours   = list.filter(d => U(d.statut) === 'EN_COURS').length;
    this.acceptes  = list.filter(d => U(d.statut) === 'ACCEPTE').length;
    this.caTotal   = list.reduce((sum, d) => sum + (d.montantHt || 0), 0);
  }

  // Pagination
  next(): void { if (!this.data || this.data.last) return; this.loadPage(this.page + 1); }
  prev(): void { if (!this.data || this.data.first) return; this.loadPage(this.page - 1); }

  // Titre & quantité
  titleFromDevis(d: DevisDTO): string {
    const l = d.lignes?.[0];
    if (!l) return d.numeroDevis;
    return `${l.descriptionLigne}${l.matiere ? ' - ' + l.matiere : ''}`;
  }
  quantiteFromDevis(d: DevisDTO): number {
    const l = d.lignes?.[0];
    return l?.quantite ?? 0;
  }

  // Badge statut
  statutBadgeClass(statut: string | undefined): string {
    switch (statut) {
      case 'ENVOYE':   return 'bg-amber-100 text-amber-700';
      case 'EN_COURS': return 'bg-blue-100 text-blue-700';
      case 'ACCEPTE':  return 'bg-emerald-100 text-emerald-700';
      case 'REFUSE':   return 'bg-rose-100 text-rose-700';
      default:         return 'bg-gray-100 text-gray-700';
    }
  }

  private getId(d: DevisDTO): number {
    // adapte si ton DTO utilise un autre nom (devisId, etc.)
    return Number((d as any).id ?? (d as any).devisId);
  }
  isDeleting(d: DevisDTO): boolean {
    const id = this.getId(d);
    return !!id && this.deletingIds.has(id);
  }

  // trackBy
  trackById = (_: number, d: DevisDTO) => this.getId(d);

  // ---- SUPPRESSION ----
  onDelete(d: DevisDTO): void {
    const id = this.getId(d);
    if (!id) return;
    if (!confirm(`Supprimer le devis ${d.numeroDevis} ?`)) return;

    this.deletingIds.add(id);
    this.devisService.deleteDevis(id).subscribe({
      next: () => {
        if (this.data) {
          this.data.content = this.data.content.filter(x => this.getId(x) !== id);
          if (this.data.content.length === 0 && !this.data.first) {
            this.loadPage(this.page - 1);
          } else {
            this.computeStats(this.data.content);
            if (typeof this.data.totalElements === 'number') {
              this.data.totalElements = Math.max(0, this.data.totalElements - 1);
            }
          }
        }
        this.deletingIds.delete(id);
      },
      error: (err) => {
        this.error = err?.error?.message || 'Échec de la suppression du devis';
        this.deletingIds.delete(id);
      }
    });
  }

  // trackBy pour stabilité/perf
  //trackById = (_: number, d: DevisDTO) => d.id;
}
