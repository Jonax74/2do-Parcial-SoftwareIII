export interface Doctor {
  id: number;
  nombre: string;
  especialidad: string;
  correo: string;
  telefono: string | null;
}

export interface DoctorRegistration {
  nombre: string;
  especialidad: string;
  correo: string;
  telefono?: string;
}
