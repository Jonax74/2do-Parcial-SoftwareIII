import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { PatientRegistration, UserRegistration, UserRole } from '../../../core/auth/auth.models';
import { ButtonComponent } from '../../atoms/button/button.component';
import { FormFieldComponent } from '../../molecules/form-field/form-field.component';

type RegistrationFormValue = {
  nombre: string;
  correo: string;
  password: string;
  rol: UserRole;
  documento: string;
  telefono: string;
  fechaNacimiento: string;
};

@Component({
  selector: 'pa-user-registration-form',
  standalone: true,
  imports: [ButtonComponent, FormFieldComponent, ReactiveFormsModule, RouterLink],
  template: `
    <section class="registration-card">
      <span class="eyebrow">{{ adminMode ? 'Gestión de cuentas' : 'Registro de paciente' }}</span>
      <h1>{{ adminMode ? 'Registrar usuario' : 'Crear cuenta de paciente' }}</h1>
      <p class="intro">
        {{ adminMode
          ? 'Crea una cuenta administrativa o registra un paciente desde la clínica.'
          : 'Completa tus datos para crear tu cuenta y tu perfil de paciente.' }}
      </p>

      @if (successMessage) {
        <p class="success-message" role="status">{{ successMessage }}</p>
        @if (!adminMode) {
          <a class="sign-in-link" routerLink="/login">Ir a iniciar sesión</a>
        }
      } @else {
        <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
          <div class="form-grid">
            @if (adminMode) {
              <div class="field">
                <label for="rol">Tipo de usuario</label>
                <select id="rol" formControlName="rol" (change)="updateDocumentValidation()">
                  <option value="PACIENTE">Paciente</option>
                  <option value="ADMINISTRADOR">Administrador</option>
                </select>
              </div>
            }

            <pa-form-field
              inputId="nombre"
              label="Nombre completo"
              placeholder="Nombre y apellidos"
              autocomplete="name"
              [control]="form.controls.nombre"
              validationMessage="El nombre es obligatorio."
            />
            <pa-form-field
              inputId="registro-correo"
              label="Correo electrónico"
              type="email"
              placeholder="nombre@correo.com"
              autocomplete="email"
              [control]="form.controls.correo"
              validationMessage="Ingresa un correo electrónico válido."
            />
            <pa-form-field
              inputId="registro-password"
              label="Contraseña"
              type="password"
              placeholder="Mínimo 8 caracteres"
              autocomplete="new-password"
              [control]="form.controls.password"
              validationMessage="La contraseña debe tener entre 8 y 72 caracteres."
            />

            @if (isPatient) {
              <pa-form-field
                inputId="documento"
                label="Documento"
                placeholder="Número de documento"
                autocomplete="off"
                [control]="form.controls.documento"
                validationMessage="El documento es obligatorio."
              />
              <pa-form-field
                inputId="telefono"
                label="Teléfono (opcional)"
                placeholder="Número de contacto"
                autocomplete="tel"
                [control]="form.controls.telefono"
              />
              <div class="field">
                <label for="fechaNacimiento">Fecha de nacimiento (opcional)</label>
                <input id="fechaNacimiento" type="date" formControlName="fechaNacimiento" />
              </div>
            }
          </div>

          @if (errorMessage) {
            <p class="error-message" role="alert">{{ errorMessage }}</p>
          }

          <pa-button type="submit" [disabled]="submitting">
            {{ submitting ? 'Guardando…' : (adminMode ? 'Registrar usuario' : 'Crear cuenta') }}
          </pa-button>
        </form>

        @if (!adminMode) {
          <p class="existing-account">¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
        }
      }
    </section>
  `,
  styleUrl: './user-registration-form.component.scss',
})
export class UserRegistrationFormComponent {
  @Input() adminMode = false;

  protected readonly form = new FormGroup({
    nombre: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(120)],
    }),
    correo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email, Validators.maxLength(150)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8), Validators.maxLength(72)],
    }),
    rol: new FormControl<UserRole>('PACIENTE', { nonNullable: true }),
    documento: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(30)],
    }),
    telefono: new FormControl('', { nonNullable: true, validators: [Validators.maxLength(30)] }),
    fechaNacimiento: new FormControl('', { nonNullable: true }),
  });

  protected submitting = false;
  protected errorMessage = '';
  protected successMessage = '';
  private readonly auth = inject(AuthService);

  protected get isPatient(): boolean {
    return !this.adminMode || this.form.controls.rol.value === 'PACIENTE';
  }

  protected updateDocumentValidation(): void {
    const documentControl = this.form.controls.documento;
    documentControl.setValidators(
      this.isPatient
        ? [Validators.required, Validators.maxLength(30)]
        : [Validators.maxLength(30)],
    );
    documentControl.updateValueAndValidity();
  }

  protected submit(): void {
    this.updateDocumentValidation();
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.submitting = true;
    const value = this.form.getRawValue();
    const request = this.adminMode
      ? this.auth.registerUser(this.toAdminRegistration(value))
      : this.auth.registerPatient(this.toPatientRegistration(value));

    request.pipe(finalize(() => this.submitting = false)).subscribe({
      next: (user) => {
        this.successMessage = this.adminMode
          ? `La cuenta de ${user.nombre} se registró correctamente.`
          : 'Tu cuenta y perfil de paciente se crearon correctamente.';
      },
      error: (error: unknown) => {
        this.errorMessage = this.readErrorMessage(error);
      },
    });
  }

  private toPatientRegistration(value: RegistrationFormValue): PatientRegistration {
    return {
      nombre: value.nombre,
      correo: value.correo,
      password: value.password,
      documento: value.documento,
      telefono: value.telefono || undefined,
      fechaNacimiento: value.fechaNacimiento || undefined,
    };
  }

  private toAdminRegistration(value: RegistrationFormValue): UserRegistration {
    const registration: UserRegistration = {
      nombre: value.nombre,
      correo: value.correo,
      password: value.password,
      rol: value.rol,
    };
    if (value.rol === 'PACIENTE') {
      registration.documento = value.documento;
      registration.telefono = value.telefono || undefined;
      registration.fechaNacimiento = value.fechaNacimiento || undefined;
    }
    return registration;
  }

  private readErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && typeof error.error?.message === 'string') {
      return error.error.message;
    }
    return 'No se pudo registrar la cuenta. Verifica los datos e inténtalo de nuevo.';
  }
}
