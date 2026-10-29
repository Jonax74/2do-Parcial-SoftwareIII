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
    path: 'registro',
    loadComponent: () =>
      import('./pages/register/register-page.component').then(
        (module) => module.RegisterPageComponent,
      ),
  },
  {
    path: 'pacientes',
    loadComponent: () =>
      import('./pages/patients/public-patients-page.component').then(
        (module) => module.PublicPatientsPageComponent,
      ),
  },
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
    path: 'admin/usuarios/nuevo',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMINISTRADOR' },
    loadComponent: () =>
      import('./pages/register/admin-register-page.component').then(
        (module) => module.AdminRegisterPageComponent,
      ),
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
