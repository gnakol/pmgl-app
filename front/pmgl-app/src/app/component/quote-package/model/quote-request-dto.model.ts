export interface QuoteRequestDTO {
  id: number;
  clientId: number;
  statut: string;
  notesGlobales?: string;
  createdAt: string; // ISO

  client: {
    civility?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    telephone?: string;
    adresse?: string;
    typeClient?: string;
    raisonSociale?: string;
    siret?: string;
  };

  items: Array<{
    id: number;
    nomPiece: string;
    typePiece?: string;
    matiere?: string;
    dimensions?: string;
    tolerance?: string;
    finition?: string;
    traitement?: string;
    quantite: number;
    delaiSouhaite?: string; // ISO date
    descriptionLigne?: string;
    urgence?: boolean;
  }>;

  files?: Array<{
    id: number;
    fileName: string;
    fileType?: string;
    description?: string;
    filePath?: string;
    mimeType?: string;
    sizeBytes?: number;
    uploadedAt?: string;
    itemIndex?: number;
    itemId?: number;
  }>;
}

// Page Spring standard
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // page index
  size: number;
  first: boolean;
  last: boolean;
}
