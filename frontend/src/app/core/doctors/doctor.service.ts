import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Doctor, DoctorRegistration } from './doctor.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class DoctorService {
  private readonly http = inject(HttpClient);

  listPublic(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${API_URL}/medicos/publico`);
  }

  listForAdministration(): Observable<Doctor[]> {
    return this.http.get<Doctor[]>(`${API_URL}/admin/medicos`);
  }

  register(doctor: DoctorRegistration): Observable<Doctor> {
    return this.http.post<Doctor>(`${API_URL}/admin/medicos`, doctor);
  }
}
