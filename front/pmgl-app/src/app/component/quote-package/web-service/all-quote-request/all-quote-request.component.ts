import { Component, OnInit } from '@angular/core';
import { QuoteRequestDTO } from '../../model/quote-request-dto.model';
import { QuoteService } from '../../service/quote.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DevisService } from '../../../devis-package/service/devis.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DevisModalComponent } from '../../../devis-package/devis-modal/devis-modal.component';
import { AuthAdminService } from '../../../admin-package/security/connect/auth-admin.service';
import { AdminService } from '../../../admin-package/security/service/admin.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-all-quote-request',
  standalone: true,
  templateUrl: './all-quote-request.component.html',
  styleUrls: ['./all-quote-request.component.scss'],
  imports : [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule
  ]
})
export class AllQuoteRequestComponent implements OnInit {
  loading = false;
  error?: string;
  quotes: QuoteRequestDTO[] = [];

  constructor(
    private quoteSrv: QuoteService,
    private devisSrv: DevisService,
    private dialog : MatDialog,
    private authAdmin : AuthAdminService,
    private adminService : AdminService
  ) {}

  ngOnInit(): void {
    this.fetch();
  }

  private fetch() {
    this.loading = true;
    this.error = undefined;
    this.quoteSrv.getAllQuoteRequests(0, 10).subscribe({
      next: (page) => {
        this.quotes = page.content ?? [];
        this.loading = false;
      },
      error: (e) => {
        this.error = 'Impossible de charger les demandes de devis.';
        this.loading = false;
        console.error(e);
      }
    });
  }

  // --- helpers d’affichage (inchangés)
  firstItem(q: QuoteRequestDTO) { return (q.items && q.items.length) ? q.items[0] : undefined; }
  title(q: QuoteRequestDTO): string {
    const it = this.firstItem(q);
    const m = it?.matiere ? ` - ${it.matiere}` : '';
    return `${it?.nomPiece ?? 'Pièce'}${m}`;
  }
  clientName(q: QuoteRequestDTO): string {
    const c = q.client || {};
    return c.raisonSociale?.trim()
      || [c.firstName, c.lastName].filter(Boolean).join(' ').trim()
      || 'Client';
  }
  qty(q: QuoteRequestDTO): number { return this.firstItem(q)?.quantite ?? 0; }
  dueDate(q: QuoteRequestDTO): string | undefined { return this.firstItem(q)?.delaiSouhaite; }
  urgenceText(q: QuoteRequestDTO): { text: string; cls: string } {
    const u = !!this.firstItem(q)?.urgence;
    return u ? { text: 'Élevée',  cls: 'text-red-600' } : { text: 'Normale', cls: 'text-green-600' };
  }
  filesCount(q: QuoteRequestDTO): number { return q.files?.length ?? 0; }
  statusPill(q: QuoteRequestDTO) {
    const s = (q.statut || '').toLowerCase();
    if (s.includes('cours')) return { text: 'En cours', bg: 'bg-blue-100 text-blue-800', leftBorder: 'border-l-4 border-l-blue-500', cta: { label: 'Générer Devis', cls: 'bg-green-600 hover:bg-green-700' } };
    if (s.includes('accept')) return { text: 'Accepté', bg: 'bg-green-100 text-green-800', leftBorder: 'border-l-4 border-l-green-500', cta: { label: 'Ouvrir', cls: 'bg-blue-600 hover:bg-blue-700' } };
    return { text: 'En attente', bg: 'bg-orange-100 text-orange-800', leftBorder: 'border-l-4 border-l-orange-500', cta: { label: 'Traiter', cls: 'bg-blue-600 hover:bg-blue-700' } };
  }

  // 💥 Méthode pour gérer le clic sur "Traiter"
// MODIFIE LA MÉTHODE handleTraiter
async handleTraiter(q: QuoteRequestDTO) {
  const items = (q.items || []).map(it => ({
    nomPiece: it.nomPiece,
    descriptionLigne: it.descriptionLigne,
    quantite: it.quantite,
    matiere: it.matiere,
    dimensions: it.dimensions,
    tolerance: it.tolerance
  }));

  // Récupérer l'email de l'admin connecté
  const adminEmail = this.authAdmin.getEmailFromToken();
  if (!adminEmail) {
    alert('❌ Impossible de récupérer votre email. Veuillez vous reconnecter.');
    return;
  }

  try {
    // Récupérer l'ID admin depuis l'email
    const adminId = await firstValueFrom(this.adminService.getAdminIdByEmail(adminEmail));

    console.log("voici ID amdin : ", adminId);
    
    const dlg = this.dialog.open(DevisModalComponent, {
      width: '900px',
      data: {
        clientId: q.clientId,
        quoteRequestId: q.id,
        clientName: this.clientName(q),
        items
      }
    });

    dlg.afterClosed().subscribe((result) => {
      if (!result || !result.lignes) return;

      this.devisSrv.createDevis(q.clientId, adminId, q.id, result.lignes).subscribe({
        next: (res) => {
          alert(`✅ Devis créé et envoyé pour ${this.clientName(q)} !`);
          console.log('Devis créé:', res);
          this.fetch();
        },
        error: (err) => {
          console.error(err);
          alert('❌ Erreur lors de la création du devis.');
        }
      });
    });

  } catch (error) {
    console.error('Erreur récupération ID admin:', error);
    alert('❌ Erreur lors de la récupération de votre profil admin.');
  }
}
}
