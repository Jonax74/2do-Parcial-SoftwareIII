import { Component } from '@angular/core';
import { PatientListComponent } from '../../shared/organisms/patient-list/patient-list.component';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';

@Component({
  selector: 'pa-admin-patients-page',
  standalone: true,
  imports: [AppShellComponent, PatientListComponent],
  template: `
    <pa-app-shell>
      <pa-patient-list scope="admin" />
    </pa-app-shell>
  `,
})
export class AdminPatientsPageComponent {
}
