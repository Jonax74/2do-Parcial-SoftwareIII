import { Component, Input, OnInit, inject } from '@angular/core';
import { PatientService } from '../../../core/patients/patient.service';
import { AdminPatient, PublicPatient, TableColumn, TableRow } from '../../../core/patients/patient.models';
import { TableSectionComponent } from '../../molecules/table-section/table-section.component';

type PatientListScope = 'public' | 'admin';

@Component({
  selector: 'pa-patient-list',
  standalone: true,
  imports: [TableSectionComponent],
  template: `
    <div class="page-intro">
      <span class="eyebrow">Clínica Piedra Azul</span>
      <h1>{{ scope === 'admin' ? 'Gestión de pacientes' : 'Nuestros pacientes' }}</h1>
      <p>
        {{ scope === 'admin'
          ? 'Consulta la información registrada para brindar una atención coordinada.'
          : 'Directorio público de pacientes registrados en la clínica.' }}
      </p>
    </div>

    @if (loading) {
      <p class="status" role="status">Cargando pacientes…</p>
    } @else if (errorMessage) {
      <p class="status error" role="alert">{{ errorMessage }}</p>
    } @else {
      <pa-table-section
        [title]="scope === 'admin' ? 'Pacientes registrados' : 'Directorio'"
        [description]="scope === 'admin'
          ? 'Información disponible para el equipo administrativo.'
          : 'El listado público contiene únicamente nombre y documento.'"
        [columns]="columns"
        [rows]="rows"
      />
    }
  `,
  styleUrl: './patient-list.component.scss',
})
export class PatientListComponent implements OnInit {
  @Input() scope: PatientListScope = 'public';

  protected loading = true;
  protected errorMessage = '';
  protected rows: TableRow[] = [];
  protected columns: TableColumn[] = [];

  private readonly patientService = inject(PatientService);

  ngOnInit(): void {
    this.columns = this.scope === 'admin'
      ? [
          { key: 'nombre', label: 'Nombre' },
          { key: 'documento', label: 'Documento' },
          { key: 'correo', label: 'Correo' },
          { key: 'telefono', label: 'Teléfono' },
          { key: 'fechaNacimiento', label: 'Fecha de nacimiento' },
        ]
      : [
          { key: 'nombre', label: 'Nombre' },
          { key: 'documento', label: 'Documento' },
        ];

    const patients$ = this.scope === 'admin'
      ? this.patientService.listForAdministration()
      : this.patientService.listPublic();

    patients$.subscribe({
      next: (patients) => {
        this.rows = patients.map((patient) => this.toRow(patient));
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No fue posible cargar el listado. Intenta de nuevo más tarde.';
        this.loading = false;
      },
    });
  }

  private toRow(patient: PublicPatient | AdminPatient): TableRow {
    if ('id' in patient) {
      return {
        nombre: patient.nombre,
        documento: patient.documento,
        correo: patient.correo,
        telefono: patient.telefono ?? '',
        fechaNacimiento: patient.fechaNacimiento ?? '',
      };
    }

    return {
      nombre: patient.nombre,
      documento: patient.documento,
    };
  }
}
