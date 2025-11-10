import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';

export interface DevisModalData {
  clientId: number;
  quoteRequestId: number;
  clientName: string;
  items: Array<{
    nomPiece: string;
    descriptionLigne?: string;
    quantite: number;
    matiere?: string;
    dimensions?: string;
    tolerance?: string;
  }>;
}

@Component({
  selector: 'app-devis-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './devis-modal.component.html'
})
export class DevisModalComponent {
  form: FormGroup;

  get lignes(): FormArray<FormGroup> {
    return this.form.get('lignes') as FormArray<FormGroup>;
  }

  getControl(group: FormGroup, controlName: string): FormControl {
    return group.get(controlName) as FormControl;
  }

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<DevisModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DevisModalData
  ) {
    this.form = this.fb.group({
      lignes: this.fb.array([])
    });

    // Pré-remplir à partir des items de la demande
    (data.items || []).forEach(it => {
      this.lignes.push(this.fb.group({
        descriptionLigne: new FormControl<string>(it.descriptionLigne || it.nomPiece, { nonNullable: true, validators: [Validators.required] }),
        quantite:        new FormControl<number>(it.quantite ?? 1, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
        prixUnitaireHt:  new FormControl<number>(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
        matiere:         new FormControl<string>(it.matiere || '', { nonNullable: true }),
        dimensions:      new FormControl<string>(it.dimensions || '', { nonNullable: true }),
        delaiFabrication:new FormControl<string>('', { nonNullable: true }),
        notes:           new FormControl<string>(it.tolerance || '', { nonNullable: true })
      }));
    });

    // Si pas d’items, une ligne vide
    if (this.lignes.length === 0) {
      this.addLigne();
    }
  }

  addLigne() {
    this.lignes.push(this.fb.group({
      descriptionLigne: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
      quantite:        new FormControl<number>(1, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
      prixUnitaireHt:  new FormControl<number>(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
      matiere:         new FormControl<string>('', { nonNullable: true }),
      dimensions:      new FormControl<string>('', { nonNullable: true }),
      delaiFabrication:new FormControl<string>('', { nonNullable: true }),
      notes:           new FormControl<string>('', { nonNullable: true })
    }));
  }

  removeLigne(i: number) {
    this.lignes.removeAt(i);
  }

  totalHT(): number {
    return this.lignes.controls.reduce((sum, g) => {
      const q = g.get('quantite')!.value || 0;
      const p = g.get('prixUnitaireHt')!.value || 0;
      return sum + q * p;
    }, 0);
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close({
      lignes: this.form.value.lignes
    });
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
