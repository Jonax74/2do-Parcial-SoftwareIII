export type AppointmentStatus = 'PROGRAMADA' | 'COMPLETADA' | 'CANCELADA';

export interface Appointment {
  id: number;
  pacienteId: number;
  pacienteNombre: string;
  pacienteDocumento: string;
  medicoId: number;
  medicoNombre: string;
  especialidad: string;
  fechaHora: string;
  motivo: string;
  estado: AppointmentStatus;
}

export interface AppointmentRegistration {
  pacienteId: number;
  medicoId: number;
  fechaHora: string;
  motivo: string;
}

export interface CreatedAppointment {
  id: number;
  pacienteId: number;
  medicoId: number;
  fechaHora: string;
  motivo: string;
  estado: AppointmentStatus;
}
