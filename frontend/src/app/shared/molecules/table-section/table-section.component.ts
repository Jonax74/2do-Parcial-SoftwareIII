import { Component, Input } from '@angular/core';
import { TableColumn, TableRow } from '../../../core/patients/patient.models';
import { DataTableComponent } from '../../atoms/data-table/data-table.component';

@Component({
  selector: 'pa-table-section',
  standalone: true,
  imports: [DataTableComponent],
  template: `
    <section class="section-card">
      <div class="section-heading">
        <div>
          <span class="eyebrow">Directorio de pacientes</span>
          <h2>{{ title }}</h2>
          <p>{{ description }}</p>
        </div>
        <span class="count">{{ rows.length }} {{ rows.length === 1 ? 'registro' : 'registros' }}</span>
      </div>
      <pa-data-table [columns]="columns" [rows]="rows" [emptyMessage]="emptyMessage" />
    </section>
  `,
  styleUrl: './table-section.component.scss',
})
export class TableSectionComponent {
  @Input({ required: true }) title = '';
  @Input() description = '';
  @Input() columns: TableColumn[] = [];
  @Input() rows: TableRow[] = [];
  @Input() emptyMessage = 'No hay pacientes registrados.';
}
