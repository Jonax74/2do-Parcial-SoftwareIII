import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { UserRegistrationFormComponent } from '../../shared/organisms/user-registration-form/user-registration-form.component';

@Component({
  selector: 'pa-admin-patient-register-page',
  standalone: true,
  imports: [AppShellComponent, UserRegistrationFormComponent],
  templateUrl: './admin-patient-register-page.component.html',
})
export class AdminPatientRegisterPageComponent {}
