import { Component, ElementRef, EventEmitter, HostListener, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { QuoteService } from '../service/quote.service';
import {
  CIVILITES,
  TYPE_CLIENTS,
  FILE_TYPES,
  CreateQuoteRequestDTO,
  UiFileDTO,
  BackendFileDTO
} from '../model/create-quote-request.model';

@Component({
  selector: 'app-quote-request',
  templateUrl: './quote-request.component.html',
  styleUrls: ['./quote-request.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  standalone: true
})
export class QuoteRequestComponent {
  @Output() closed = new EventEmitter<void>();
  @ViewChild('modalContent') modalContent!: ElementRef;
  @ViewChild('fileInput') fileInput!: ElementRef;

  // === ÉTATS UI ===
  currentStep = 1;
  isVisible = false;
  isSubmitting = false;
  isDragOver = false;

  // === LISTES (locales pour éviter toute erreur d'import) ===
  civilities = CIVILITES;
  clientTypes = TYPE_CLIENTS;

  // Ex-constantes: on les garde localement
  materials = [
    'Acier', 'Inox', 'Aluminium', 'Laiton', 'Bronze', 'Plastique', 'Autre'
  ];
  typePieces = [
    'Axe', 'Support', 'Engrenage', 'Poulie', 'Arbre', 'Carter',
    'Bride', 'Plaque', 'Rondelle', 'Vis', 'Écrou', 'Autre'
  ];
  dimensions = [
    '≤ 50mm', '50-100mm', '100-200mm', '200-500mm', '≥ 500mm', 'Sur mesure'
  ];
  tolerances = [
    'Standard (±0.5mm)', 'Précision (±0.1mm)', 'Haute précision (±0.05mm)',
    'Très haute précision (±0.01mm)', 'Spécifique'
  ];
  finitions = [
    'Brut de usinage', 'Poli', 'Brossé', 'Sablé', 'Anodisé', 'Galvanisé', 'Peint', 'Autre'
  ];
  traitements = [
    'Aucun', 'Trempe', 'Revenu', 'Recuit', 'Nitruration', 'Chromage', 'Zingage', 'Autre'
  ];
  // NOTE: on garde ces libellés si ton template affiche encore une liste.
  // Quand tu passeras à <input type="date">, tu pourras supprimer ce tableau.
  delaisSouhaites = [
    'Standard (2-3 semaines)',
    'Urgent (1 semaine)',
    'Très urgent (48-72h)',
    'Spécifique (date précise)'
  ];

  fileTypes = FILE_TYPES;

  // === FICHIERS (UI) ===
  selectedFiles: UiFileDTO[] = [];

  // === FORM ===
  quoteForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private quoteService: QuoteService
  ) {
    this.quoteForm = this.createForm();
  }

  ngOnInit() {
    setTimeout(() => (this.isVisible = true), 10);
  }

  // ---------- FORM ----------
  createForm(): FormGroup {
    return this.fb.group({
      // Étape 1
      civility: ['', Validators.required],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.required, Validators.pattern(/^[0-9+\s()-]{10,}$/)]],
      // ⚠️ backend attend 'particulier' | 'entreprise' en minuscule
      typeClient: ['particulier', Validators.required],
      raisonSociale: [''],
      siret: [''],

      // Étape 2
      nomPiece: ['', Validators.required],
      typePiece: [''],
      matiere: [''],
      dimensions: [''],
      tolerance: [''],
      finition: [''],
      traitement: [''],
      quantite: [1, [Validators.required, Validators.min(1)]],
      descriptionLigne: [''],

      // Étape 4
      // ⚠️ backend = LocalDate ('YYYY-MM-DD'). Si tu gardes la liste pour l’instant,
      // laisse une chaîne; dès que tu passeras sur input date, il faudra envoyer 'YYYY-MM-DD'.
      delaiSouhaite: [''],
      urgence: [false],
      notesGlobales: ['']
    });
  }

  // ---------- NAV ----------
  nextStep(): void {
    if (this.isStepValid()) this.currentStep++;
  }

  previousStep(): void {
    this.currentStep--;
  }

  isStepValid(): boolean {
    switch (this.currentStep) {
      case 1:
        return !!this.quoteForm.get('civility')?.valid &&
               !!this.quoteForm.get('firstName')?.valid &&
               !!this.quoteForm.get('lastName')?.valid &&
               !!this.quoteForm.get('email')?.valid &&
               !!this.quoteForm.get('telephone')?.valid &&
               !!this.quoteForm.get('typeClient')?.valid;
      case 2:
        return !!this.quoteForm.get('nomPiece')?.valid &&
               !!this.quoteForm.get('quantite')?.valid;
      case 3:
      case 4:
      case 5:
        return true;
      default:
        return false;
    }
  }

  // ---------- FILES ----------
  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    this.addFiles(files);
  }

  onFileDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    const files = event.dataTransfer?.files;
    if (files) this.addFiles(files);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
  }

  addFiles(files: FileList): void {
    const maxSize = 20 * 1024 * 1024; // 20MB
    const allowed = ['.pdf', '.dwg', '.dxf', '.step', '.stp', '.iges', '.igs', '.stl', '.jpg', '.jpeg', '.png'];

    for (let i = 0; i < files.length; i++) {
      const file = files[i];

      if (file.size > maxSize) {
        alert(`❌ Le fichier "${file.name}" dépasse 20MB`);
        continue;
      }
      const ext = '.' + this.getFileExtension(file.name).toLowerCase();
      if (!allowed.includes(ext)) {
        alert(`❌ Le format "${ext}" n'est pas supporté pour "${file.name}"`);
        continue;
      }

      this.selectedFiles.push({
        file,
        // on stocke un type par défaut, éditable dans l’UI
        fileType: 'AUTRE',
        description: ''
      });
    }

    if (this.fileInput) this.fileInput.nativeElement.value = '';
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  isFileTypeAllowed(file: File): boolean {
    const extension = '.' + this.getFileExtension(file.name).toLowerCase();
    const allowed = ['.pdf', '.dwg', '.dxf', '.step', '.stp', '.iges', '.igs', '.stl', '.jpg', '.jpeg', '.png'];
    return (
      allowed.includes(extension) ||
      file.type === '' ||
      file.type.includes('image/') ||
      file.type.includes('application/')
    );
  }

  getFileExtension(filename: string): string {
    return filename.split('.').pop()?.toUpperCase() || 'FILE';
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  private fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const res = reader.result as string;
        const base64 = res.includes('base64,') ? res.split('base64,')[1] : res;
        resolve(base64);
      };
      reader.onerror = (e) => reject(e);
      reader.readAsDataURL(file);
    });
  }

  private async buildBackendFiles(): Promise<BackendFileDTO[] | undefined> {
    if (this.selectedFiles.length === 0) return undefined;

    const out: BackendFileDTO[] = [];
    for (const f of this.selectedFiles) {
      const contentBase64 = await this.fileToBase64(f.file);
      out.push({
        fileName: f.file.name,
        fileType: f.fileType,      // 'PLAN_2D' | 'MODELE_3D' | 'PHOTO' | 'AUTRE'
        description: f.description || '',
        contentBase64,
        itemIndex: null            // tu pourras lier à items[i] plus tard si besoin
      });
    }
    return out;
  }

  // ---------- SUBMIT ----------
  async onSubmit(): Promise<void> {
    if (!this.quoteForm.valid) return;

    this.isSubmitting = true;
    try {
      const formValue = this.quoteForm.value;
      const files = await this.buildBackendFiles(); // conversion en base64 ici

      const request: CreateQuoteRequestDTO = {
        applicant: {
          civility: formValue.civility,
          firstName: formValue.firstName,
          lastName: formValue.lastName,
          email: formValue.email,
          telephone: formValue.telephone,
          typeClient: formValue.typeClient, // 'particulier' | 'entreprise'
          raisonSociale: formValue.raisonSociale || undefined,
          siret: formValue.siret || undefined
        },
        notesGlobales: formValue.notesGlobales || undefined,
        items: [
          {
            nomPiece: formValue.nomPiece,
            typePiece: formValue.typePiece || undefined,
            matiere: formValue.matiere || undefined,
            dimensions: formValue.dimensions || undefined,
            tolerance: formValue.tolerance || undefined,
            finition: formValue.finition || undefined,
            traitement: formValue.traitement || undefined,
            quantite: formValue.quantite,
            // ⚠️ idéalement: 'YYYY-MM-DD'. Si tu gardes le select, laisse vide ou mappe vers une date plus tard.
            delaiSouhaite: formValue.delaiSouhaite || undefined,
            descriptionLigne: formValue.descriptionLigne || undefined,
            urgence: !!formValue.urgence
          }
        ],
        files
      };

      this.quoteService.createQuoteRequest(request).subscribe({
        next: (quoteId) => {
          this.isSubmitting = false;
          this.showSuccessMessage(quoteId);
          this.closeModal();
        },
        error: (error) => {
          this.isSubmitting = false;
          this.handleError(error);
        }
      });
    } catch (e) {
      this.isSubmitting = false;
      this.handleError(e);
    }
  }

  // ---------- UX ----------
  private showSuccessMessage(quoteId: number): void {
    alert(
      `✅ Demande de devis #${quoteId} envoyée avec succès !\n\n📧 Vous recevrez une réponse sous 24h ouvrables.\n\nMerci pour votre confiance !`
    );
  }

  private handleError(error: any): void {
    console.error('Erreur lors de la demande de devis:', error);

    if (error?.status === 0) {
      alert('❌ Impossible de contacter le serveur. Vérifiez votre connexion internet.');
    } else if (error?.status === 400) {
      alert('❌ Données invalides. Vérifiez les informations saisies.');
    } else {
      alert('❌ Erreur lors de l\'envoi de la demande. Veuillez réessayer.');
    }
  }

  // ---------- MODALE ----------
  closeModal(): void {
    this.isVisible = false;
    setTimeout(() => {
      this.closed.emit();
      this.resetForm();
    }, 300);
  }

  private resetForm(): void {
    this.currentStep = 1;
    this.selectedFiles = [];
    this.quoteForm.reset({
      typeClient: 'particulier',
      quantite: 1,
      urgence: false
    });
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeModal();
  }
}
