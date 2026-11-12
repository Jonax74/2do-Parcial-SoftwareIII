import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
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
  imports: [ButtonComponent, FormFieldComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './user-registration-form.component.html',
  styleUrl: './user-registration-form.component.css',
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
