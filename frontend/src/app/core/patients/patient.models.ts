export interface PublicPatient {
  nombre: string;
  documento: string;
}

export interface AdminPatient extends PublicPatient {
  id: number;
  correo: string;
  telefono: string | null;
  fechaNacimiento: string | null;
}

export interface TableColumn {
  key: string;
  label: string;
}

export type TableRow = Record<string, string>;
