import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminPatient, PublicPatient } from './patient.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private readonly http = inject(HttpClient);

  listPublic(): Observable<PublicPatient[]> {
    return this.http.get<PublicPatient[]>(`${API_URL}/pacientes/publico`);
  }

  listForAdministration() {
    return this.http.get<AdminPatient[]>(`${API_URL}/admin/pacientes`);
  }
}
