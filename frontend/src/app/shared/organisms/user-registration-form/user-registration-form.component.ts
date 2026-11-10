import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input, OnInit, inject } from '@angular/core';
import {
  AsyncValidatorFn,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { catchError, finalize, map, of, tap } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { PatientRegistration, UserRegistration, UserRole } from '../../../core/auth/auth.models';
import { nonBlank, pastDate } from '../../../core/forms/form-validators';
import { PatientService } from '../../../core/patients/patient.service';
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
  imports: [ButtonComponent, FormFieldComponent, ReactiveFormsModule],
  template: `
    <section class="registration-card">
      <span class="eyebrow">{{ adminMode ? 'Gestión de cuentas' : 'Registro de paciente' }}</span>
      <h1>{{ patientOnly ? 'Registrar paciente' : (adminMode ? 'Registrar usuario' : 'Crear cuenta de paciente') }}</h1>
      <p class="intro">
        {{ patientOnly
          ? 'Registra los datos del paciente. La cédula se verifica con el directorio antes de guardar.'
          : (adminMode
          ? 'Crea una cuenta administrativa o registra un paciente desde la clínica.'
          : 'Completa tus datos para crear tu cuenta y tu perfil de paciente.') }}
      </p>

      @if (successMessage) {
        <p class="success-message" role="status">{{ successMessage }}</p>
      } @else {
        <form [formGroup]="form" (ngSubmit)="submit()" novalidate>
          <div class="form-grid">
            @if (adminMode && !patientOnly) {
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
              [maxLength]="120"
              [control]="form.controls.nombre"
              validationMessage="El nombre es obligatorio y debe tener entre 2 y 120 caracteres."
            />
            <pa-form-field
              inputId="registro-correo"
              label="Correo electrónico"
              type="email"
              placeholder="nombre@correo.com"
              autocomplete="email"
              [maxLength]="150"
              [control]="form.controls.correo"
              validationMessage="Ingresa un correo válido de máximo 150 caracteres."
            />
            <pa-form-field
              inputId="registro-password"
              label="Contraseña"
              type="password"
              placeholder="Mínimo 8 caracteres"
              autocomplete="new-password"
              [maxLength]="72"
              [control]="form.controls.password"
              validationMessage="La contraseña debe tener entre 8 y 72 caracteres."
            />

            @if (isPatient) {
              <pa-form-field
                inputId="documento"
                label="Documento"
                placeholder="Número de documento"
                autocomplete="off"
                [maxLength]="15"
                inputMode="numeric"
                [digitsOnly]="true"
                [control]="form.controls.documento"
                [validationMessage]="documentValidationMessage"
              />
              @if (form.controls.documento.hasError('documentExists')) {
                <p class="error-message" role="alert">Ya existe un paciente registrado con esta cédula.</p>
              } @else if (form.controls.documento.hasError('documentLookupUnavailable')) {
                <p class="error-message" role="alert">No fue posible verificar la cédula. Intenta de nuevo.</p>
              } @else if (form.controls.documento.pending) {
                <p class="checking-message" role="status">Verificando cédula…</p>
              }
              <pa-form-field
                inputId="telefono"
                label="Teléfono (opcional)"
                placeholder="Número de contacto"
                autocomplete="tel"
                [maxLength]="15"
                inputMode="numeric"
                [digitsOnly]="true"
                [control]="form.controls.telefono"
                validationMessage="El teléfono debe contener entre 7 y 15 dígitos."
              />
              <div class="field">
                <label for="fechaNacimiento">Fecha de nacimiento (opcional)</label>
                <input id="fechaNacimiento" type="date" [max]="today" formControlName="fechaNacimiento" />
                @if (form.controls.fechaNacimiento.touched && form.controls.fechaNacimiento.invalid) {
                  <small class="field-error">La fecha de nacimiento debe ser anterior a hoy.</small>
                }
              </div>
            }
          </div>

          @if (errorMessage) {
            <p class="error-message" role="alert">{{ errorMessage }}</p>
          }

          <pa-button type="submit" [disabled]="submitting">
            {{ submitting ? 'Guardando…' : (patientOnly ? 'Registrar paciente' : (adminMode ? 'Registrar usuario' : 'Crear cuenta')) }}
          </pa-button>
        </form>

      }
    </section>
  `,
  styleUrl: './user-registration-form.component.scss',
})
export class UserRegistrationFormComponent implements OnInit {
  @Input() adminMode = true;
  @Input() patientOnly = false;

  private readonly auth = inject(AuthService);
  private readonly patientService = inject(PatientService);
  private readonly changeDetector = inject(ChangeDetectorRef);
  private readonly documentValidator: AsyncValidatorFn = (control) =>
    this.patientService.documentExists(String(control.value).trim()).pipe(
    map((exists): ValidationErrors | null => exists ? { documentExists: true } : null),
    catchError(() => of({ documentLookupUnavailable: true })),
    tap(() => this.changeDetector.markForCheck()),
  );

  protected readonly form = new FormGroup({
    nombre: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.minLength(2), Validators.maxLength(120)],
    }),
    correo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.email, Validators.maxLength(150)],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(8), Validators.maxLength(72)],
    }),
    rol: new FormControl<UserRole>('PACIENTE', { nonNullable: true }),
    documento: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(6), Validators.maxLength(15), Validators.pattern(/^\d+$/)],
      updateOn: 'blur',
    }),
    telefono: new FormControl('', { nonNullable: true, validators: [Validators.pattern(/^\d{7,15}$/)] }),
    fechaNacimiento: new FormControl('', { nonNullable: true, validators: [pastDate] }),
  });

  protected submitting = false;
  protected errorMessage = '';
  protected successMessage = '';
  protected readonly today = this.getLocalDate();

  ngOnInit(): void {
    this.updateDocumentValidation();
  }

  protected get isPatient(): boolean {
    return this.patientOnly || !this.adminMode || this.form.controls.rol.value === 'PACIENTE';
  }

  protected get documentValidationMessage(): string {
    const document = this.form.controls.documento;
    if (document.hasError('required')) {
      return 'La cédula es obligatoria.';
    }
    if (document.hasError('minlength') || document.hasError('maxlength')) {
      return 'La cédula debe tener entre 6 y 15 dígitos.';
    }
    return 'La cédula solo puede contener dígitos.';
  }

  protected updateDocumentValidation(): void {
    const documentControl = this.form.controls.documento;
    documentControl.setValidators(
      this.isPatient
        ? [Validators.required, Validators.minLength(6), Validators.maxLength(15), Validators.pattern(/^\d+$/)]
        : [],
    );
    documentControl.setAsyncValidators(this.isPatient ? [this.documentValidator] : []);
    documentControl.updateValueAndValidity();
  }

  protected submit(): void {
    if (this.form.invalid || this.form.pending) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.submitting = true;
    const value = this.form.getRawValue();
    const request = this.patientOnly || value.rol === 'PACIENTE'
      ? this.auth.registerPatient(this.toPatientRegistration(value))
      : this.auth.registerUser(this.toAdminRegistration(value));

    request.pipe(finalize(() => {
      this.submitting = false;
      this.changeDetector.markForCheck();
    })).subscribe({
      next: (user) => {
        this.successMessage = !this.patientOnly && user.rol === 'ADMINISTRADOR'
          ? `La cuenta de ${user.nombre} se registró correctamente.`
          : `${user.nombre} se registró correctamente como paciente.`;
        this.changeDetector.markForCheck();
      },
      error: (error: unknown) => {
        this.errorMessage = this.readErrorMessage(error);
        this.changeDetector.markForCheck();
      },
    });
  }

  private toPatientRegistration(value: RegistrationFormValue): PatientRegistration {
    return {
      nombre: value.nombre,
      correo: value.correo.trim().toLowerCase(),
      password: value.password,
      documento: value.documento,
      telefono: value.telefono || undefined,
      fechaNacimiento: value.fechaNacimiento || undefined,
    };
  }

  private getLocalDate(): string {
    const date = new Date();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${month}-${day}`;
  }

  private toAdminRegistration(value: RegistrationFormValue): UserRegistration {
    const registration: UserRegistration = {
      nombre: value.nombre,
      correo: value.correo.trim().toLowerCase(),
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
