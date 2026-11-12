import { Component, Input } from '@angular/core';
import { TableColumn, TableRow } from '../../../core/patients/patient.models';
import { DataTableComponent } from '../../atoms/data-table/data-table.component';

@Component({
  selector: 'pa-table-section',
  standalone: true,
  imports: [DataTableComponent],
  templateUrl: './table-section.component.html',
  styleUrl: './table-section.component.css',
})
export class TableSectionComponent {
  @Input({ required: true }) title = '';
  @Input() eyebrow = 'Directorio clínico';
  @Input() description = '';
  @Input() columns: TableColumn[] = [];
  @Input() rows: TableRow[] = [];
  @Input() emptyMessage = 'No hay pacientes registrados.';
}
