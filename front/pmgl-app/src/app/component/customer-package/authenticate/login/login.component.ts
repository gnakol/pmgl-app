import { Component, EventEmitter, HostListener, OnDestroy, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoginService } from '../core/login.service';
import { AuthDTO } from '../bean/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class LoginComponent implements OnInit, OnDestroy {
  @Output() closed = new EventEmitter<void>();

  form: FormGroup;
  loading = false;
  errorMsg: string | null = null;
  isVisible = false;

  constructor(private fb: FormBuilder, private loginService: LoginService) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Empêche le scroll derrière la modale
    document.body.classList.add('overflow-hidden');
    // petite anim
    setTimeout(() => (this.isVisible = true), 10);
  }

  ngOnDestroy(): void {
    document.body.classList.remove('overflow-hidden');
  }

  // Ferme sur ESC
  @HostListener('document:keydown.escape')
  onEscape() {
    this.close();
  }

  close() {
    this.isVisible = false;
    setTimeout(() => this.closed.emit(), 200);
  }

  submit(): void {
    this.errorMsg = null;
    if (this.form.invalid) return;

    this.loading = true;
    const payload: AuthDTO = this.form.value;

    this.loginService.login(payload).subscribe({
      next: () => {
        this.loading = false;
        this.close(); // ferme la modale si login OK
      },
      error: (err) => {
        this.loading = false;
        if (err?.status === 401) this.errorMsg = 'Identifiants invalides.';
        else if (err?.status === 403) this.errorMsg = "Accès refusé.";
        else this.errorMsg = "Une erreur est survenue. Réessayez.";
      }
    });
  }
}
