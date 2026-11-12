import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { PatientService } from '../../../core/patients/patient.service';
import { AdminPatient, TableColumn, TableRow } from '../../../core/patients/patient.models';
import { RouterLink } from '@angular/router';
import { ButtonComponent } from '../../atoms/button/button.component';
import { TableSectionComponent } from '../../molecules/table-section/table-section.component';

@Component({
  selector: 'pa-patient-list',
  standalone: true,
  imports: [ButtonComponent, RouterLink, TableSectionComponent],
  templateUrl: './patient-list.component.html',
  styleUrl: './patient-list.component.css',
})
export class PatientListComponent implements OnInit {
  protected loading = true;
  protected errorMessage = '';
  protected rows: TableRow[] = [];
  protected readonly columns: TableColumn[] = [
    { key: 'nombre', label: 'Nombre' },
    { key: 'documento', label: 'Documento' },
    { key: 'correo', label: 'Correo' },
    { key: 'telefono', label: 'Teléfono' },
    { key: 'fechaNacimiento', label: 'Nacimiento' },
  ];

  private readonly patientService = inject(PatientService);
  private readonly changeDetector = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadPatients();
  }

  protected loadPatients(): void {
    this.loading = true;
    this.errorMessage = '';
    this.patientService.listForAdministration().subscribe({
      next: (patients) => {
        this.rows = patients.map((patient) => this.toRow(patient));
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.errorMessage = 'No fue posible cargar el directorio. Verifica tu conexión e inténtalo de nuevo.';
        this.loading = false;
        this.changeDetector.markForCheck();
      },
    });
  }

  private toRow(patient: AdminPatient): TableRow {
    return {
      nombre: patient.nombre,
      documento: patient.documento,
      correo: patient.correo,
      telefono: patient.telefono ?? '',
      fechaNacimiento: patient.fechaNacimiento ?? '',
    };
  }
}
