export interface QuoteLigneDTO {
  id: number;
  descriptionLigne: string;
  quantite: number;
  prixUnitaireHt: number;
  matiere?: string | null;
  dimensions?: string | null;
  delaiFabrication?: string | null;
  notes?: string | null;
  createdAt: string; // ISO date
}
