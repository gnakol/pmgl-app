import { Component, EventEmitter, Output, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthAdminService } from '../connect/auth-admin.service';

@Component({
  selector: 'app-login-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-admin.component.html',
  styleUrls: ['./login-admin.component.scss']
})
export class LoginAdminComponent {
  @Output() closed = new EventEmitter<void>();

  form: FormGroup;
  loading = false;
  errorMsg: string | null = null;

  constructor(private fb: FormBuilder, private auth: AuthAdminService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  // ESC pour fermer
  @HostListener('document:keydown.escape') onEsc() {
    this.close();
  }

  close() {
    this.closed.emit();
  }

  submit(): void {
    this.errorMsg = null;
    if (this.form.invalid) return;

    this.loading = true;
    const { email, password } = this.form.value;

    this.auth.login(email, password).subscribe({
      next: () => {
        this.loading = false;
        if (!this.auth.isAdmin()) {
          this.errorMsg = "Accès réservé à l'administrateur.";
          this.auth.logout();
          return;
        }
        this.close();
        this.router.navigate(['/dashboard-admin']);
      },
      error: (err) => {
        this.loading = false;
        if (err?.status === 401) this.errorMsg = 'Identifiants invalides.';
        else if (err?.status === 403) this.errorMsg = 'Accès refusé.';
        else this.errorMsg = 'Une erreur est survenue. Réessayez.';
      }
    });
  }
}
