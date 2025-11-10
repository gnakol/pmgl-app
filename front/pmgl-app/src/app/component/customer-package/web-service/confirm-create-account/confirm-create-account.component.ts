import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from '../../service/customer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-create-account',
  standalone: true,    
  templateUrl: './confirm-create-account.component.html',
  styleUrls: ['./confirm-create-account.component.scss'],
  imports : [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class ConfirmCreateAccountComponent {
  @Output() closed = new EventEmitter<void>();

  passwordForm: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private customerService: CustomerService
  ) {
    this.passwordForm = this.fb.group({
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        this.passwordValidator
      ]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  // Validateur personnalisé pour les critères du mot de passe
  private passwordValidator(control: AbstractControl) {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumber = /[0-9]/.test(value);
    const hasSpecialChar = /[@$!%*?&]/.test(value);

    if (!hasUpperCase || !hasLowerCase || !hasNumber || !hasSpecialChar) {
      return { passwordStrength: true };
    }
    return null;
  }

  // Validateur pour vérifier que les mots de passe correspondent
  private passwordMatchValidator(group: AbstractControl) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    
    return password === confirmPassword ? null : { passwordsMismatch: true };
  }

  // Méthodes pour basculer la visibilité des mots de passe
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  // Vérification des critères de mot de passe pour l'affichage
  getPasswordRequirement(type: string): { met: boolean } {
    const password = this.passwordForm.get('password')?.value || '';
    
    switch (type) {
      case 'minLength':
        return { met: password.length >= 8 };
      case 'uppercase':
        return { met: /[A-Z]/.test(password) };
      case 'lowercase':
        return { met: /[a-z]/.test(password) };
      case 'number':
        return { met: /[0-9]/.test(password) };
      case 'special':
        return { met: /[@$!%*?&]/.test(password) };
      default:
        return { met: false };
    }
  }

  // Soumission du formulaire
  async onSubmit() {
    if (this.passwordForm.valid) {
      this.loading = true;
      
      try {
        // Récupérer le token depuis l'URL ou le stockage
        const token = this.getTokenFromUrl(); // À implémenter selon ta logique
        
        const request = {
          token: token,
          password: this.passwordForm.value.password
        };

        // Appel au service pour confirmer l'invitation
        await this.customerService.confirmInvitation(request).toPromise();
        
        // Redirection vers le dashboard client
        this.router.navigate(['/template-customer']);
        
      } catch (error) {
        console.error('Erreur lors de la création du compte:', error);
        // Gérer l'erreur (afficher un message à l'utilisateur)
      } finally {
        this.loading = false;
      }
    }
  }

  onCancel() {
    this.closed.emit();
  }

  // Méthode pour récupérer le token (à adapter selon ton implémentation)
  private getTokenFromUrl(): string {
    // Exemple: récupérer depuis les paramètres d'URL
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('token') || '';
  }
}