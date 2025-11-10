import { AccountDTO } from "./account-dto.model";

export interface CustomerDTO {
  id: number;
  accountId: number;
  typeClient: 'PARTICULIER' | 'ENTREPRISE' | 'STARTUP';
  raisonSociale: string | null;
  siret: string | null;
  adresse: string;
  telephone: string;
  createdAt: string;
}

// customer-dto.model.ts
export interface CustomerWithAccount extends CustomerDTO {
  account?: AccountDTO;
  projetsActifs?: number;
  caAnnuel?: number;
  statutFidelite?: 'Fidèle' | 'Nouveau' | 'Occasionnel';
  sector?: string;          
}
