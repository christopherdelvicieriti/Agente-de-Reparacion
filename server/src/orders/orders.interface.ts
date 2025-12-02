import { Client } from '../clients/clients.interface';

export interface Order {
  id: number;
  client: Client;
  fechaCreacion: Date;
  extension_tiempo: number | null;
  detalle: string | null;
  entrega: string | null;
  estado: boolean;
  cobrado: boolean;
  total: number | null;
}