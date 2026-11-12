import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { Doctor } from '../../../core/doctors/doctor.models';
import { DoctorService } from '../../../core/doctors/doctor.service';
import { nonBlank } from '../../../core/forms/form-validators';
import { TableColumn, TableRow } from '../../../core/patients/patient.models';
import { ButtonComponent } from '../../atoms/button/button.component';
import { DigitsOnlyDirective } from '../../directives/digits-only.directive';
import { TableSectionComponent } from '../../molecules/table-section/table-section.component';

@Component({
  selector: 'pa-doctor-directory',
  standalone: true,
  imports: [ButtonComponent, DigitsOnlyDirective, ReactiveFormsModule, TableSectionComponent],
  templateUrl: './doctor-directory.component.html',
  styleUrl: './doctor-directory.component.css',
})
export class DoctorDirectoryComponent implements OnInit {
  @Input() adminMode = false;

  protected loading = true;
  protected saving = false;
  protected showForm = false;
  protected errorMessage = '';
  protected formError = '';
  protected formSuccess = '';
  protected rows: TableRow[] = [];
  protected readonly columns: TableColumn[] = [
    { key: 'nombre', label: 'Profesional' },
    { key: 'especialidad', label: 'Especialidad' },
    { key: 'correo', label: 'Correo' },
    { key: 'telefono', label: 'Teléfono' },
  ];
  protected readonly form = new FormGroup({
    nombre: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.minLength(2), Validators.maxLength(120)],
    }),
    especialidad: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.minLength(2), Validators.maxLength(100)],
    }),
    correo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.email, Validators.maxLength(150)],
    }),
    telefono: new FormControl('', {
      nonNullable: true,
      validators: [Validators.pattern(/^\d{7,15}$/)],
    }),
  });

  private readonly doctorService = inject(DoctorService);
  private readonly changeDetector = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadDoctors();
  }

  protected loadDoctors(): void {
    this.loading = true;
    this.errorMessage = '';
    const request = this.adminMode
      ? this.doctorService.listForAdministration()
      : this.doctorService.listPublic();
    request.subscribe({
      next: (doctors) => {
        this.rows = doctors.map((doctor) => this.toRow(doctor));
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.errorMessage = 'No fue posible cargar el directorio médico. Intenta de nuevo.';
        this.loading = false;
        this.changeDetector.markForCheck();
      },
    });
  }

  protected register(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;
    this.formError = '';
    this.formSuccess = '';
    const value = this.form.getRawValue();
    this.doctorService.register({
      nombre: value.nombre.trim(),
      especialidad: value.especialidad.trim(),
      correo: value.correo.trim().toLowerCase(),
      telefono: value.telefono.trim() || undefined,
    }).pipe(finalize(() => {
      this.saving = false;
      this.changeDetector.markForCheck();
    })).subscribe({
      next: (doctor) => {
        this.formSuccess = `${doctor.nombre} se agregó al directorio médico.`;
        this.form.reset();
        this.changeDetector.markForCheck();
        this.loadDoctors();
      },
      error: (error: unknown) => {
        this.formError = error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
          ? error.error.message
          : 'No se pudo registrar el médico. Revisa los datos e inténtalo de nuevo.';
        this.changeDetector.markForCheck();
      },
    });
  }

  private toRow(doctor: Doctor): TableRow {
    return {
      nombre: doctor.nombre,
      especialidad: doctor.especialidad,
      correo: doctor.correo,
      telefono: doctor.telefono ?? '',
    };
  }
}
