import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { AuthResponse } from '../../core/auth/auth.models';
import { ButtonComponent } from '../../shared/atoms/button/button.component';
import { FormFieldComponent } from '../../shared/molecules/form-field/form-field.component';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';

@Component({
  selector: 'pa-login-page',
  standalone: true,
  imports: [AppShellComponent, ButtonComponent, FormFieldComponent, ReactiveFormsModule, RouterLink],
  template: `
    <pa-app-shell>
      <section class="login-layout">
        <div class="welcome-panel">
          <span class="welcome-icon" aria-hidden="true">✚</span>
          <span class="eyebrow">Bienvenido a Piedra Azul</span>
          <h1>Cuidamos de ti en cada etapa.</h1>
          <p>
            Ingresa a tu cuenta para acceder a los servicios de la clínica de forma segura.
          </p>
          <div class="welcome-note">
            <span aria-hidden="true">♡</span>
            Atención humana, segura y cercana.
          </div>
        </div>

        <section class="login-card" aria-labelledby="login-title">
          <span class="eyebrow">Acceso seguro</span>
          <h2 id="login-title">Inicia sesión</h2>
          <p class="hint">Usa el correo y la contraseña de tu cuenta.</p>

          <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
            <pa-form-field
              inputId="correo"
              label="Correo electrónico"
              type="email"
              placeholder="nombre@correo.com"
              autocomplete="username"
              [control]="form.controls.correo"
              validationMessage="Ingresa un correo electrónico válido."
            />
            <pa-form-field
              inputId="password"
              label="Contraseña"
              type="password"
              placeholder="Tu contraseña"
              autocomplete="current-password"
              [control]="form.controls.password"
              validationMessage="La contraseña es obligatoria."
            />

            @if (errorMessage) {
              <p class="form-error" role="alert">{{ errorMessage }}</p>
            }

            <pa-button type="submit" [disabled]="submitting">
              {{ submitting ? 'Ingresando…' : 'Iniciar sesión' }}
            </pa-button>
          </form>

          <p class="register-link">
            ¿Aún no tienes cuenta? <a routerLink="/registro">Regístrate como paciente</a>
          </p>

          <p class="privacy-note">
            <span aria-hidden="true">●</span>
            Tu información está protegida.
          </p>
        </section>
      </section>
    </pa-app-shell>
  `,
  styleUrl: './login-page.component.scss',
})
export class LoginPageComponent {
  protected readonly form = new FormGroup({
    correo: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });
  protected submitting = false;
  protected errorMessage = '';

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.submitting = true;
    this.auth.login(this.form.getRawValue())
      .pipe(finalize(() => this.submitting = false))
      .subscribe({
        next: (response) => this.navigateAfterLogin(response),
        error: () => {
          this.errorMessage = 'No pudimos validar tus datos. Revisa el correo y la contraseña.';
        },
      });
  }

  private navigateAfterLogin(response: AuthResponse): void {
    const destination = response.usuario.rol === 'ADMINISTRADOR'
      ? '/admin/pacientes'
      : '/pacientes';
    void this.router.navigateByUrl(destination);
  }
}
