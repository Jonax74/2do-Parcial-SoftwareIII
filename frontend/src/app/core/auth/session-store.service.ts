import { computed, Injectable, signal } from '@angular/core';
import { UserRole } from './auth.models';

const TOKEN_KEY = 'piedra-azul.token';
const ROLE_KEY = 'piedra-azul.role';

function isUserRole(value: string | null): value is UserRole {
  return value === 'ADMINISTRADOR' || value === 'PACIENTE';
}

@Injectable({ providedIn: 'root' })
export class SessionStore {
  private readonly tokenState = signal<string | null>(this.read(TOKEN_KEY));
  private readonly roleState = signal<UserRole | null>(this.readRole());
  readonly authenticated = computed(() => this.tokenState() !== null);

  token(): string | null {
    return this.tokenState();
  }

  role(): UserRole | null {
    return this.roleState();
  }

  save(token: string, role: UserRole): void {
    if (typeof window !== 'undefined') {
      window.sessionStorage.setItem(TOKEN_KEY, token);
      window.sessionStorage.setItem(ROLE_KEY, role);
    }
    this.tokenState.set(token);
    this.roleState.set(role);
  }

  clear(): void {
    if (typeof window !== 'undefined') {
      window.sessionStorage.removeItem(TOKEN_KEY);
      window.sessionStorage.removeItem(ROLE_KEY);
    }
    this.tokenState.set(null);
    this.roleState.set(null);
  }

  private read(key: string): string | null {
    return typeof window === 'undefined' ? null : window.sessionStorage.getItem(key);
  }

  private readRole(): UserRole | null {
    const storedRole = this.read(ROLE_KEY);
    return isUserRole(storedRole) ? storedRole : null;
  }
}
