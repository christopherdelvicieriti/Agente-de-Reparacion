import { ApiProperty } from '@nestjs/swagger';
import { Client } from '../clients/clients.entity';
import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
} from 'typeorm';

@Entity({ name: 'Orders' })
export class Order {
  @ApiProperty({ description: 'ID único de la orden', example: 1 })
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Client, {
    nullable: false,
    onDelete: 'SET NULL',
    eager: true,
  })
  @JoinColumn({ name: 'id_client' })
  @ApiProperty({
    type: () => Client,
    nullable: false,
    description: 'Cliente asociado a la orden',
  })
  client: Client | null;

  @ApiProperty({ description: 'Fecha de creación de la orden' })
  @CreateDateColumn({
    name: 'fechaCreacion',
    type: 'datetime',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fechaCreacion: Date;

  @ApiProperty({
    description: 'Tiempo extra (ej. días)',
    required: false,
    example: 5,
  })
  @Column({ name: 'extension_tiempo', type: 'integer', nullable: true })
  extension_tiempo: number;

  @ApiProperty({
    description: 'Detalles de la orden',
    required: false,
    example: 'Reparación de pantalla',
  })
  @Column({ type: 'text', nullable: true })
  detalle: string;

  @ApiProperty({
    description: 'Fecha de entrega (YYYY-MM-DD)',
    required: false,
    example: '2025-12-31',
  })
  @Column({ type: 'text', nullable: true })
  entrega: string;

  @ApiProperty({
    description: 'Indica si la orden está cancelada',
    default: false,
  })
  @Column({ type: 'boolean', default: false })
  estado: boolean;

  @ApiProperty({
    description: 'Indica si la orden está cobrada',
    default: false,
  })
  @Column({ type: 'boolean', default: false })
  cobrado: boolean;

  @ApiProperty({
    description: 'Monto total (en centavos o unidad base)',
    required: false,
    example: 5000,
  })
  @Column({ type: 'integer', nullable: true })
  total: number;
}