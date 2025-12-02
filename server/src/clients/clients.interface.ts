export interface Client {
  id: number;
  nombre: string;
  dirección: string | null;
  cedula: string | null;
  telf1: string | null;
  telf2: string | null;
  email: string | null;
  fechaCreacion: Date;
}