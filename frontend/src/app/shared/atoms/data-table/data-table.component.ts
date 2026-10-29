import { Component, Input } from '@angular/core';
import { TableColumn, TableRow } from '../../../core/patients/patient.models';

@Component({
  selector: 'pa-data-table',
  standalone: true,
  template: `
    <div class="table-scroll">
      <table>
        <thead>
          <tr>
            @for (column of columns; track column.key) {
              <th scope="col">{{ column.label }}</th>
            }
          </tr>
        </thead>
        <tbody>
          @for (row of rows; track $index) {
            <tr>
              @for (column of columns; track column.key) {
                <td [attr.data-label]="column.label">{{ row[column.key] || '—' }}</td>
              }
            </tr>
          } @empty {
            <tr>
              <td class="empty" [attr.colspan]="columns.length">{{ emptyMessage }}</td>
            </tr>
          }
        </tbody>
      </table>
    </div>
  `,
  styleUrl: './data-table.component.scss',
})
export class DataTableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() rows: TableRow[] = [];
  @Input() emptyMessage = 'No hay registros para mostrar.';
}
