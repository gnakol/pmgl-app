export interface CreateQuoteRequestDTO {
  clientId?: number;
  applicant?: ApplicantDTO;
  notesGlobales?: string;
  items: ItemDTO[];
  files?: BackendFileDTO[];
}

export interface ApplicantDTO {
  civility: string;
  firstName: string;
  lastName: string;
  email: string;
  telephone: string;
  adresse?: string;
  // enum backend: particulier | entreprise (minuscule)
  typeClient: 'particulier' | 'entreprise';
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
  // backend: LocalDate -> 'YYYY-MM-DD'
  delaiSouhaite?: string;
  descriptionLigne?: string;
  urgence?: boolean;
}

/** Fichiers attendus par le backend */
export interface BackendFileDTO {
  fileName: string;
  fileType: 'PLAN_2D' | 'MODELE_3D' | 'PHOTO' | 'AUTRE';
  description?: string;
  contentBase64: string;
  itemIndex?: number | null;
}

/** Fichiers côté UI (avant conversion) */
export interface UiFileDTO {
  file: File;
  fileType: 'PLAN_2D' | 'MODELE_3D' | 'PHOTO' | 'AUTRE';
  description?: string;
  itemIndex?: number | null;
}

export const TYPE_CLIENTS = [
  { value: 'particulier', label: 'Particulier' },
  { value: 'entreprise',  label: 'Entreprise' }
];

export const CIVILITES = [
  { value: 'MR',  label: 'Monsieur' },
  { value: 'MME', label: 'Madame' }
];

export const FILE_TYPES = [
  { value: 'PLAN_2D',   label: 'Plan 2D (DWG, DXF, PDF)' },
  { value: 'MODELE_3D', label: 'Modèle 3D (STEP, IGES, STL)' },
  { value: 'PHOTO',     label: 'Photo/Référence' },
  { value: 'AUTRE',     label: 'Autre document' }
];
