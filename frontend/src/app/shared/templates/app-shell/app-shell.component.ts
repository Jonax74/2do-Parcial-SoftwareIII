import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ButtonComponent } from '../../atoms/button/button.component';

@Component({
  selector: 'pa-app-shell',
  standalone: true,
  imports: [ButtonComponent, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <header class="site-header">
        <a class="brand" routerLink="/pacientes" aria-label="Clínica Piedra Azul, inicio">
          <span class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 48 48" role="img">
              <path d="M20 8h8v12h12v8H28v12h-8V28H8v-8h12z" />
            </svg>
          </span>
          <span class="brand-copy">
            <strong>Clínica Piedra Azul</strong>
            <small>Salud y cuidado cercano</small>
          </span>
        </a>
        <nav aria-label="Navegación principal">
          <a routerLink="/pacientes" routerLinkActive="active">Pacientes</a>
          @if (auth.isAuthenticated()) {
            @if (auth.role() === 'ADMINISTRADOR') {
              <a routerLink="/admin/pacientes" routerLinkActive="active">Administración</a>
              <a routerLink="/admin/usuarios/nuevo" routerLinkActive="active">Registrar usuario</a>
            }
            <pa-button variant="secondary" (click)="logout()">Cerrar sesión</pa-button>
          } @else {
            <a routerLink="/registro" routerLinkActive="active">Crear cuenta</a>
            <a class="login-link" routerLink="/login" routerLinkActive="active">Iniciar sesión</a>
          }
        </nav>
      </header>

      <main>
        <ng-content />
      </main>

      <footer>
        <span>Clínica Piedra Azul</span>
        <span>Atención con confianza, siempre.</span>
      </footer>
    </div>
  `,
  styleUrl: './app-shell.component.scss',
})
export class AppShellComponent {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }
}
