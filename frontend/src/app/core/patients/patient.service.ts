import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminPatient } from './patient.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private readonly http = inject(HttpClient);

  listForAdministration() {
    return this.http.get<AdminPatient[]>(`${API_URL}/admin/pacientes`);
  }

  documentExists(document: string): Observable<boolean> {
    return this.http.get<boolean>(`${API_URL}/admin/pacientes/existe`, {
      params: { documento: document },
    });
  }
}
