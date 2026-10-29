import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import {
  AuthResponse,
  LoginCredentials,
  PatientRegistration,
  RegisteredUser,
  UserRegistration,
  UserRole,
} from './auth.models';
import { SessionStore } from './session-store.service';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = inject(SessionStore);

  login(credentials: LoginCredentials) {
    return this.http.post<AuthResponse>(`${API_URL}/auth/login`, credentials).pipe(
      tap((response) => this.session.save(response.token, response.usuario.rol)),
    );
  }

  registerPatient(registration: PatientRegistration): Observable<RegisteredUser> {
    return this.http.post<RegisteredUser>(`${API_URL}/auth/register`, registration);
  }

  registerUser(registration: UserRegistration): Observable<RegisteredUser> {
    return this.http.post<RegisteredUser>(`${API_URL}/admin/usuarios`, registration);
  }

  isAuthenticated(): boolean {
    return this.session.authenticated();
  }

  role(): UserRole | null {
    return this.session.role();
  }

  logout(): void {
    this.session.clear();
  }
}
