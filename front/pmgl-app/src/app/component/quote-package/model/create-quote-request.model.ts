export interface CreateQuoteRequestDTO {
  clientId?: number;
  applicant?: ApplicantDTO;
  notesGlobales?: string;
  items: ItemDTO[];
}

export interface ApplicantDTO {
  civility: string;
  firstName: string;
  lastName: string;
  email: string;
  telephone: string;
  adresse?: string;
  typeClient: 'PARTICULIER' | 'ENTREPRISE';
  raisonSociale?: string;
  siret?: string;
}

export interface ItemDTO {
  nomPiece: string;
  typePiece?: string;
  matiere?: string;
  dimensions?: string;
  tolerance?: string;
  finition?: string;
  traitement?: string;
  quantite: number;
  delaiSouhaite?: string;
  descriptionLigne?: string;
  urgence?: boolean;
}

export const MATIERES = [
  'Acier', 'Inox', 'Aluminium', 'Laiton', 'Bronze', 'Plastique'
];

export const TYPE_CLIENTS = [
  { value: 'PARTICULIER', label: 'Particulier' },
  { value: 'ENTREPRISE', label: 'Entreprise' }
];

export const CIVILITES = [
  { value: 'MR', label: 'Monsieur' },
  { value: 'MME', label: 'Madame' }
];