export interface Space {
  id: number;
  alias: string;
  descripcion: string | null;
  image: string | null;
  color: string | null;
  fechaCreacion: Date;
}