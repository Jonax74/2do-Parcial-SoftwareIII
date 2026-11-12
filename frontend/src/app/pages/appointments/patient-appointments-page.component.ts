import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { AppointmentDashboardComponent } from '../../shared/organisms/appointment-dashboard/appointment-dashboard.component';

@Component({
  selector: 'pa-patient-appointments-page',
  standalone: true,
  imports: [AppShellComponent, AppointmentDashboardComponent],
  templateUrl: './patient-appointments-page.component.html',
})
export class PatientAppointmentsPageComponent {}
