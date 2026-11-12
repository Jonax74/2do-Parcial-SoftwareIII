import { Component, Input } from '@angular/core';
import { TableColumn, TableRow } from '../../../core/patients/patient.models';

@Component({
  selector: 'pa-data-table',
  standalone: true,
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.component.css',
})
export class DataTableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() rows: TableRow[] = [];
  @Input() emptyMessage = 'No hay registros para mostrar.';
}
