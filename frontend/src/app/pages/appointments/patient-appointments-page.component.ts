import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { AppointmentDashboardComponent } from '../../shared/organisms/appointment-dashboard/appointment-dashboard.component';

@Component({
  selector: 'pa-patient-appointments-page',
  standalone: true,
  imports: [AppShellComponent, AppointmentDashboardComponent],
  template: `
    <pa-app-shell>
      <pa-appointment-dashboard />
    </pa-app-shell>
  `,
})
export class PatientAppointmentsPageComponent {}
