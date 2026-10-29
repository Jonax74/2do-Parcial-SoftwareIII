export type UserRole = 'ADMINISTRADOR' | 'PACIENTE';

export interface LoginCredentials {
  correo: string;
  password: string;
}

export interface PatientRegistration {
  nombre: string;
  correo: string;
  password: string;
  documento: string;
  telefono?: string;
  fechaNacimiento?: string;
}

export interface UserRegistration {
  nombre: string;
  correo: string;
  password: string;
  rol: UserRole;
  documento?: string;
  telefono?: string;
  fechaNacimiento?: string;
}

export interface RegisteredUser {
  id: number;
  nombre: string;
  correo: string;
  rol: UserRole;
}

export interface AuthResponse {
  token: string;
  tipo: 'Bearer';
  expiraEn: string;
  usuario: {
    id: number;
    nombre: string;
    correo: string;
    rol: UserRole;
  };
}
