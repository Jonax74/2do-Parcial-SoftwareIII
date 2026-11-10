import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, Input, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';
import { Appointment } from '../../../core/appointments/appointment.models';
import { AppointmentService } from '../../../core/appointments/appointment.service';
import { Doctor } from '../../../core/doctors/doctor.models';
import { DoctorService } from '../../../core/doctors/doctor.service';
import { futureDateTime, nonBlank } from '../../../core/forms/form-validators';
import { AdminPatient, TableColumn, TableRow } from '../../../core/patients/patient.models';
import { PatientService } from '../../../core/patients/patient.service';
import { ButtonComponent } from '../../atoms/button/button.component';
import { TableSectionComponent } from '../../molecules/table-section/table-section.component';

@Component({
  selector: 'pa-appointment-dashboard',
  standalone: true,
  imports: [ButtonComponent, ReactiveFormsModule, TableSectionComponent],
  template: `
    <section class="page-intro">
      <div>
        <span class="eyebrow">{{ adminMode ? 'Área administrativa' : 'Portal del paciente' }}</span>
        <h1>{{ adminMode ? 'Gestión de citas' : 'Mis citas médicas' }}</h1>
        <p>{{ adminMode
          ? 'Programa nuevas consultas y revisa la agenda de todos los pacientes.'
          : 'Aquí puedes consultar las citas asociadas a tu cuenta.' }}</p>
      </div>
      @if (adminMode) {
        <pa-button (click)="showForm = !showForm">
          {{ showForm ? 'Cerrar formulario' : 'Crear cita' }}
        </pa-button>
      }
    </section>

    @if (adminMode && showForm) {
      <section class="form-card">
        <span class="eyebrow">Agenda clínica</span>
        <h2>Programar una cita</h2>
        @if (optionsError) {
          <p class="form-error" role="alert">{{ optionsError }}</p>
        } @else {
          <form [formGroup]="form" (ngSubmit)="create()" novalidate>
            <label>
              Paciente
              <select formControlName="pacienteId">
                <option value="">Selecciona un paciente</option>
                @for (patient of patients; track patient.id) {
                  <option [value]="patient.id">{{ patient.nombre }} · {{ patient.documento }}</option>
                }
              </select>
              @if (form.controls.pacienteId.touched && form.controls.pacienteId.invalid) {
                <small class="field-error">Selecciona un paciente.</small>
              }
            </label>
            <label>
              Médico
              <select formControlName="medicoId">
                <option value="">Selecciona un médico</option>
                @for (doctor of doctors; track doctor.id) {
                  <option [value]="doctor.id">{{ doctor.nombre }} · {{ doctor.especialidad }}</option>
                }
              </select>
              @if (form.controls.medicoId.touched && form.controls.medicoId.invalid) {
                <small class="field-error">Selecciona un médico.</small>
              }
            </label>
            <label>
              Fecha y hora
              <input type="datetime-local" formControlName="fechaHora" />
              @if (form.controls.fechaHora.touched && form.controls.fechaHora.invalid) {
                <small class="field-error">Elige una fecha futura.</small>
              }
            </label>
            <label>
              Motivo de consulta
              <textarea rows="3" minlength="5" maxlength="500" formControlName="motivo" placeholder="Describe brevemente el motivo"></textarea>
              @if (form.controls.motivo.touched && form.controls.motivo.invalid) {
                <small class="field-error">
                  @if (form.controls.motivo.hasError('blank') || form.controls.motivo.hasError('required')) {
                    El motivo es obligatorio.
                  } @else if (form.controls.motivo.hasError('minlength')) {
                    El motivo debe tener al menos 5 caracteres.
                  } @else {
                    El motivo no puede superar 500 caracteres.
                  }
                </small>
              }
            </label>
            @if (formError) {
              <p class="form-error" role="alert">{{ formError }}</p>
            }
            @if (formSuccess) {
              <p class="form-success" role="status">{{ formSuccess }}</p>
            }
            <pa-button type="submit" [disabled]="saving">
              {{ saving ? 'Guardando…' : 'Confirmar cita' }}
            </pa-button>
          </form>
        }
      </section>
    }

    @if (loading) {
      <p class="status" role="status">Cargando citas…</p>
    } @else if (errorMessage) {
      <p class="status error" role="alert">{{ errorMessage }}</p>
      <pa-button variant="secondary" (click)="loadAppointments()">Intentar de nuevo</pa-button>
    } @else {
      <pa-table-section
        [eyebrow]="adminMode ? 'Agenda institucional' : 'Atención médica'"
        [title]="adminMode ? 'Citas registradas' : 'Próximas consultas'"
        [description]="adminMode
          ? 'Agenda ordenada por fecha y hora de atención.'
          : 'Las citas mostradas pertenecen a tu usuario autenticado.'"
        [columns]="columns"
        [rows]="rows"
        emptyMessage="No hay citas para mostrar."
      />
    }
  `,
  styleUrl: './appointment-dashboard.component.scss',
})
export class AppointmentDashboardComponent implements OnInit {
  @Input() adminMode = false;

  protected loading = true;
  protected saving = false;
  protected showForm = false;
  protected errorMessage = '';
  protected optionsError = '';
  protected formError = '';
  protected formSuccess = '';
  protected doctors: Doctor[] = [];
  protected patients: AdminPatient[] = [];
  protected rows: TableRow[] = [];
  protected get columns(): TableColumn[] {
    return [
      { key: 'fechaHora', label: 'Fecha y hora' },
      ...(this.adminMode ? [{ key: 'pacienteNombre', label: 'Paciente' }] : []),
      { key: 'medicoNombre', label: 'Médico' },
      { key: 'especialidad', label: 'Especialidad' },
      { key: 'motivo', label: 'Motivo' },
      { key: 'estado', label: 'Estado' },
    ];
  }
  protected readonly form = new FormGroup({
    pacienteId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    medicoId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    fechaHora: new FormControl('', { nonNullable: true, validators: [Validators.required, futureDateTime] }),
    motivo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, nonBlank, Validators.minLength(5), Validators.maxLength(500)],
    }),
  });

  private readonly appointmentService = inject(AppointmentService);
  private readonly doctorService = inject(DoctorService);
  private readonly patientService = inject(PatientService);
  private readonly changeDetector = inject(ChangeDetectorRef);

  ngOnInit(): void {
    if (this.adminMode) {
      this.loadOptions();
    }
    this.loadAppointments();
  }

  protected loadAppointments(): void {
    this.loading = true;
    this.errorMessage = '';
    const request = this.adminMode
      ? this.appointmentService.listAll()
      : this.appointmentService.listMine();
    request.subscribe({
      next: (appointments) => {
        this.rows = appointments.map((appointment) => this.toRow(appointment));
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.errorMessage = 'No fue posible cargar las citas. Verifica tu conexión e inténtalo de nuevo.';
        this.loading = false;
        this.changeDetector.markForCheck();
      },
    });
  }

  protected create(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const date = new Date(value.fechaHora);
    if (Number.isNaN(date.getTime()) || date.getTime() <= Date.now()) {
      this.form.controls.fechaHora.setErrors({ future: true });
      this.form.controls.fechaHora.markAsTouched();
      return;
    }
    this.saving = true;
    this.formError = '';
    this.formSuccess = '';
    this.appointmentService.create({
      pacienteId: Number(value.pacienteId),
      medicoId: Number(value.medicoId),
      fechaHora: date.toISOString(),
      motivo: value.motivo.trim(),
    }).pipe(finalize(() => {
      this.saving = false;
      this.changeDetector.markForCheck();
    })).subscribe({
      next: () => {
        this.formSuccess = 'La cita quedó programada correctamente.';
        this.form.reset();
        this.changeDetector.markForCheck();
        this.loadAppointments();
      },
      error: (error: unknown) => {
        this.formError = error instanceof HttpErrorResponse && typeof error.error?.message === 'string'
          ? error.error.message
          : 'No se pudo crear la cita. Verifica los datos e inténtalo de nuevo.';
        this.changeDetector.markForCheck();
      },
    });
  }

  private loadOptions(): void {
    forkJoin({
      doctors: this.doctorService.listForAdministration(),
      patients: this.patientService.listForAdministration(),
    }).subscribe({
      next: ({ doctors, patients }) => {
        this.doctors = doctors;
        this.patients = patients;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.optionsError = 'No fue posible cargar médicos y pacientes para crear una cita.';
        this.changeDetector.markForCheck();
      },
    });
  }

  private toRow(appointment: Appointment): TableRow {
    return {
      fechaHora: new Intl.DateTimeFormat('es-CO', {
        dateStyle: 'medium',
        timeStyle: 'short',
      }).format(new Date(appointment.fechaHora)),
      pacienteNombre: appointment.pacienteNombre,
      medicoNombre: appointment.medicoNombre,
      especialidad: appointment.especialidad,
      motivo: appointment.motivo,
      estado: appointment.estado.toLowerCase(),
    };
  }
}
