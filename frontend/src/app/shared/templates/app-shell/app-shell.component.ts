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
        <a class="brand" routerLink="/medicos" aria-label="Clínica Piedra Azul, inicio">
          <span class="brand-mark">
            <img src="/images/clinica-symbol.png" alt="" />
          </span>
          <span class="brand-copy">
            <strong>Clínica Piedra Azul</strong>
            <small>Tu salud, nuestra prioridad</small>
          </span>
        </a>
        <nav aria-label="Navegación principal">
          @if (auth.isAuthenticated()) {
            @if (auth.role() === 'ADMINISTRADOR') {
              <a routerLink="/admin/medicos" routerLinkActive="active">Médicos</a>
              <a routerLink="/admin/pacientes" routerLinkActive="active">Pacientes</a>
              <a routerLink="/admin/citas" routerLinkActive="active">Citas</a>
            }
            @if (auth.role() === 'PACIENTE') {
              <a routerLink="/medicos" routerLinkActive="active">Médicos</a>
              <a routerLink="/paciente/citas" routerLinkActive="active">Mis citas</a>
            }
            <pa-button variant="secondary" (click)="logout()">Cerrar sesión</pa-button>
          } @else {
            <a routerLink="/medicos" routerLinkActive="active">Médicos</a>
            <a class="login-link" routerLink="/login" routerLinkActive="active">Iniciar sesión</a>
          }
        </nav>
      </header>

      <main>
        <ng-content />
      </main>

      <footer>
        <div class="footer-brand">
          <img src="/images/clinica-symbol.png" alt="" />
          <span>Clínica Piedra Azul</span>
        </div>
        <span>Atención cercana, con confianza y cuidado.</span>
        <span class="footer-location">Popayán · Cauca</span>
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
    void this.router.navigateByUrl('/medicos');
  }
}
