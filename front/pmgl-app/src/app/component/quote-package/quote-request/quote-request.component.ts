import { Component, ElementRef, EventEmitter, HostListener, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { QuoteService } from '../service/quote.service';
import { CIVILITES, CreateQuoteRequestDTO, MATIERES, TYPE_CLIENTS } from '../model/create-quote-request.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quote-request',
  templateUrl: './quote-request.component.html',
  styleUrls: ['./quote-request.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class QuoteRequestComponent {
  @Output() closed = new EventEmitter<void>();
  @ViewChild('modalContent') modalContent!: ElementRef;

  currentStep = 1;
  isVisible = false;
  isSubmitting = false;

  civilities = CIVILITES;
  clientTypes = TYPE_CLIENTS;
  materials = MATIERES;

  quoteForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private quoteService: QuoteService
  ) {
    this.quoteForm = this.createForm();
  }

  ngOnInit() {
    // Animation d'entrée
    setTimeout(() => this.isVisible = true, 10);
  }

  createForm(): FormGroup {
    return this.fb.group({
      // Step 1: Informations client
      civility: ['', Validators.required],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.required, Validators.pattern(/^[0-9+\s()-]{10,}$/)]],
      typeClient: ['PARTICULIER', Validators.required],
      raisonSociale: [''],
      siret: [''],

      // Step 2: Détails pièce
      nomPiece: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      matiere: [''],
      dimensions: [''],
      descriptionLigne: [''],
      urgence: [false],

      // Step 3: Global
      notesGlobales: ['']
    });
  }

  nextStep() {
    if (this.isStepValid()) {
      this.currentStep++;
    }
  }

  previousStep() {
    this.currentStep--;
  }

  isStepValid(): boolean {
    switch (this.currentStep) {
      case 1:
        // CORRECTION : Utilisation de !! pour convertir en boolean
        return !!this.quoteForm.get('civility')?.valid &&
               !!this.quoteForm.get('firstName')?.valid &&
               !!this.quoteForm.get('lastName')?.valid &&
               !!this.quoteForm.get('email')?.valid &&
               !!this.quoteForm.get('telephone')?.valid &&
               !!this.quoteForm.get('typeClient')?.valid;
      case 2:
        // CORRECTION : Utilisation de !! pour convertir en boolean
        return !!this.quoteForm.get('nomPiece')?.valid &&
               !!this.quoteForm.get('quantite')?.valid;
      case 3:
        return true;
      default:
        return false;
    }
  }

  onSubmit() {
    if (this.quoteForm.valid) {
      this.isSubmitting = true;

      const formValue = this.quoteForm.value;
      
      const request: CreateQuoteRequestDTO = {
        applicant: {
          civility: formValue.civility,
          firstName: formValue.firstName,
          lastName: formValue.lastName,
          email: formValue.email,
          telephone: formValue.telephone,
          typeClient: formValue.typeClient,
          raisonSociale: formValue.raisonSociale || undefined,
          siret: formValue.siret || undefined
        },
        notesGlobales: formValue.notesGlobales,
        items: [{
          nomPiece: formValue.nomPiece,
          quantite: formValue.quantite,
          matiere: formValue.matiere || undefined,
          dimensions: formValue.dimensions || undefined,
          descriptionLigne: formValue.descriptionLigne || undefined,
          urgence: formValue.urgence || false
        }]
      };

      this.quoteService.createQuoteRequest(request).subscribe({
        next: (quoteId) => {
          this.isSubmitting = false;
          alert(`Demande de devis #${quoteId} envoyée avec succès !`);
          this.closeModal();
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Erreur lors de la demande de devis:', error);
          alert('Erreur lors de l\'envoi de la demande. Veuillez réessayer.');
        }
      });
    }
  }

  closeModal() {
    this.isVisible = false;
    setTimeout(() => {
      this.closed.emit();
    }, 300);
  }

  @HostListener('document:keydown.escape')
  onEscape() {
    this.closeModal();
  }
}