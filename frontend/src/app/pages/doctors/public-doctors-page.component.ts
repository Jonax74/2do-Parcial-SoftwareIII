import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { DoctorDirectoryComponent } from '../../shared/organisms/doctor-directory/doctor-directory.component';

@Component({
  selector: 'pa-public-doctors-page',
  standalone: true,
  imports: [AppShellComponent, DoctorDirectoryComponent],
  template: `
    <pa-app-shell>
      <pa-doctor-directory />
    </pa-app-shell>
  `,
})
export class PublicDoctorsPageComponent {}
