import { Order } from '../orders/orders.interface';
import { Space } from '../spaces/spaces.interface';

export interface Machine {
  id: number;
  order: Order;
  space: Space | null;
  modelo: string;
  descripcion: string | null;
  accesorios: string | null;
  img_anverso: string | null;
  img_reverso: string | null;
  img_accesorios: string | null;
  costo_arreglo: number | null;
  fechaIngreso: Date;
}