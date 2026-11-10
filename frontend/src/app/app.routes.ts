import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login-page.component').then((module) => module.LoginPageComponent),
  },
  {
    path: 'medicos',
    loadComponent: () =>
      import('./pages/doctors/public-doctors-page.component').then(
        (module) => module.PublicDoctorsPageComponent,
      ),
  },
  { path: 'pacientes', pathMatch: 'full', redirectTo: 'medicos' },
  {
    path: 'admin/pacientes',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/patients/admin-patients-page.component').then(
        (module) => module.AdminPatientsPageComponent,
      ),
  },
  {
    path: 'admin/medicos',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/doctors/admin-doctors-page.component').then(
        (module) => module.AdminDoctorsPageComponent,
      ),
  },
  {
    path: 'admin/citas',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/appointments/admin-appointments-page.component').then(
        (module) => module.AdminAppointmentsPageComponent,
      ),
  },
  {
    path: 'admin/pacientes/nuevo',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/register/admin-patient-register-page.component').then(
        (module) => module.AdminPatientRegisterPageComponent,
      ),
  },
  {
    path: 'admin/usuarios/nuevo',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/register/admin-register-page.component').then(
        (module) => module.AdminRegisterPageComponent,
      ),
  },
  {
    path: 'paciente/citas',
    canActivate: [authGuard, roleGuard],
    data: { role: 'PACIENTE' },
    loadComponent: () =>
      import('./pages/appointments/patient-appointments-page.component').then(
        (module) => module.PatientAppointmentsPageComponent,
      ),
  },
  { path: '', pathMatch: 'full', redirectTo: 'medicos' },
  { path: '**', redirectTo: 'medicos' },
];
