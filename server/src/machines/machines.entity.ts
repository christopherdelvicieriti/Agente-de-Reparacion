import { ApiProperty } from '@nestjs/swagger';
import { Order } from '../orders/orders.entity';
import { Space } from '../spaces/spaces.entity';
import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
} from 'typeorm';

@Entity({ name: 'Machines' })
export class Machine {
  @ApiProperty({ description: 'ID único de la máquina', example: 1 })
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Order, {
    nullable: false,
    onDelete: 'CASCADE',
    eager: true,
  })
  @JoinColumn({ name: 'id_order' })
  @ApiProperty({ type: () => Order, nullable: false })
  order: Order;

  @ManyToOne(() => Space, {
    nullable: true,
    onDelete: 'CASCADE',
    eager: true,
  })
  @JoinColumn({ name: 'id_spaces' })
  @ApiProperty({ type: () => Space, nullable: true })
  space: Space | null;

  @ApiProperty({ description: 'Modelo de la máquina', example: 'Singer 2250' })
  @Column({ type: 'text', nullable: false })
  modelo: string;

  @ApiProperty({ required: false, example: 'Máquina de coser doméstica' })
  @Column({ type: 'text', nullable: true })
  descripcion: string;

  @ApiProperty({ required: false, example: 'Pedal, agujas extra' })
  @Column({ type: 'text', nullable: true })
  accesorios: string;

  @ApiProperty({ description: 'URL o path de la imagen anverso', required: false })
  @Column({ name: 'img_anverso', type: 'text', nullable: true })
  img_anverso: string;

  @ApiProperty({ description: 'URL o path de la imagen reverso', required: false })
  @Column({ name: 'img_reverso', type: 'text', nullable: true })
  img_reverso: string;

  @ApiProperty({ description: 'URL o path de la imagen de accesorios', required: false })
  @Column({ name: 'img_accesorios', type: 'text', nullable: true })
  img_accesorios: string;

  @ApiProperty({ description: 'Costo del arreglo (en centavos)', required: false })
  @Column({ name: 'costo_arreglo', type: 'integer', nullable: true })
  costo_arreglo: number;

  @ApiProperty({ description: 'Fecha de ingreso de la máquina' })
  @CreateDateColumn({
    name: 'fechaIngreso',
    type: 'datetime',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fechaIngreso: Date;
}