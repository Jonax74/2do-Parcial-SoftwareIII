import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { AuthResponse } from '../../core/auth/auth.models';
import { nonBlank } from '../../core/forms/form-validators';
import { ButtonComponent } from '../../shared/atoms/button/button.component';
import { FormFieldComponent } from '../../shared/molecules/form-field/form-field.component';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';

@Component({
  selector: 'pa-login-page',
  standalone: true,
  imports: [AppShellComponent, ButtonComponent, FormFieldComponent, ReactiveFormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
})
export class LoginPageComponent {
  protected readonly form = new FormGroup({
    correo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.email, Validators.maxLength(150)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(72)],
    }),
  });
  protected submitting = false;
  protected errorMessage = '';

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly changeDetector = inject(ChangeDetectorRef);

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.submitting = true;
    const credentials = this.form.getRawValue();
    this.auth.login({ ...credentials, correo: credentials.correo.trim().toLowerCase() })
      .pipe(finalize(() => {
        this.submitting = false;
        this.changeDetector.markForCheck();
      }))
      .subscribe({
        next: (response) => this.navigateAfterLogin(response),
        error: () => {
          this.errorMessage = 'No pudimos validar tus datos. Revisa el correo y la contraseña.';
          this.changeDetector.markForCheck();
        },
      });
  }

  private navigateAfterLogin(response: AuthResponse): void {
    const destination = response.usuario.rol === 'ADMINISTRADOR'
      ? '/admin/pacientes'
      : '/paciente/citas';
    void this.router.navigateByUrl(destination);
  }
}
