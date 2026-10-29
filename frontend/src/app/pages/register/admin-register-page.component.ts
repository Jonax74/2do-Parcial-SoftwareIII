import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { UserRegistrationFormComponent } from '../../shared/organisms/user-registration-form/user-registration-form.component';

@Component({
  selector: 'pa-admin-register-page',
  standalone: true,
  imports: [AppShellComponent, UserRegistrationFormComponent],
  template: `
    <pa-app-shell>
      <pa-user-registration-form [adminMode]="true" />
    </pa-app-shell>
  `,
})
export class AdminRegisterPageComponent {
}
