export interface AdminPatient {
  id: number;
  nombre: string;
  documento: string;
  correo: string;
  telefono: string | null;
  fechaNacimiento: string | null;
}

export interface TableColumn {
  key: string;
  label: string;
}

export type TableRow = Record<string, string>;
