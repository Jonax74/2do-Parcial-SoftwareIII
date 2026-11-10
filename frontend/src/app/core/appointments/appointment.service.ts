import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Appointment, AppointmentRegistration, CreatedAppointment } from './appointment.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private readonly http = inject(HttpClient);

  listAll(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${API_URL}/admin/citas`);
  }

  listMine(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${API_URL}/paciente/citas`);
  }

  create(appointment: AppointmentRegistration): Observable<CreatedAppointment> {
    return this.http.post<CreatedAppointment>(`${API_URL}/admin/citas`, appointment);
  }
}
