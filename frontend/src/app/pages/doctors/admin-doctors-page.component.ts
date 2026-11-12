import { Component } from '@angular/core';
import { AppShellComponent } from '../../shared/templates/app-shell/app-shell.component';
import { DoctorDirectoryComponent } from '../../shared/organisms/doctor-directory/doctor-directory.component';

@Component({
  selector: 'pa-admin-doctors-page',
  standalone: true,
  imports: [AppShellComponent, DoctorDirectoryComponent],
  templateUrl: './admin-doctors-page.component.html',
})
export class AdminDoctorsPageComponent {}
