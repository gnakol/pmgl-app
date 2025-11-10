import { QuoteLigneDTO } from './quote-lignes.model';

export type DevisStatut = 'BROUILLON' | 'ENVOYE' | 'ACCEPTE' | 'REFUSE' | 'EN_COURS' | 'ANNULE';

export interface DevisDTO {
  id: number;
  clientId: number;
  adminId: number;
  quoteRequestId: number | null;
  numeroDevis: string;
  statut: DevisStatut;
  dateCreation: string;       // ISO
  dateEnvoi?: string | null;  // ISO
  dateValidite?: string | null; // ISO
  dateModification?: string | null; // ISO
  montantHt: number;
  tva: number;
  delaiLivraison?: string | null;
  conditions?: string | null;
  fichierDevis?: string | null;
  notesAdmin?: string | null;
  lignes: QuoteLigneDTO[];
}

// Pagination Spring
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // current page (0-based)
  size: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}
