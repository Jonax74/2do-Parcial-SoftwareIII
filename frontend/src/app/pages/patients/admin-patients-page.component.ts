import { Component } from '@angular/core';
import { PatientListComponent } from '../../shared/organisms/patient-list/patient-list.component';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';

@Component({
  selector: 'pa-admin-patients-page',
  standalone: true,
  imports: [AppShellComponent, PatientListComponent],
  templateUrl: './admin-patients-page.component.html',
})
export class AdminPatientsPageComponent {
}
