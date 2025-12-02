import { ApiProperty } from '@nestjs/swagger';
import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
} from 'typeorm';

@Entity({ name: 'Clients' })
export class Client {
  @ApiProperty({ description: 'ID único del cliente', example: 1 })
  @PrimaryGeneratedColumn()
  id: number;

  @ApiProperty({ description: 'Nombre completo del cliente', example: 'Juan Pérez' })
  @Column({ type: 'text', nullable: false })
  nombre: string;

  @ApiProperty({
    description: 'Dirección física del cliente',
    example: 'Av. Siempre Viva 123',
    required: false,
  })
  @Column({ type: 'text', nullable: true })
  dirección: string;

  @ApiProperty({
    description: 'Cédula o ID legal del cliente',
    example: '1234567890',
    required: false,
  })
  @Column({ type: 'text', nullable: true })
  cedula: string;

  @ApiProperty({
    description: 'Teléfono principal',
    example: '0991234567',
    required: false,
  })
  @Column({ type: 'text', nullable: true })
  telf1: string;

  @ApiProperty({
    description: 'Teléfono secundario (opcional)',
    example: '022345678',
    required: false,
  })
  @Column({ type: 'text', nullable: true })
  telf2: string;

  @ApiProperty({
    description: 'Correo electrónico del cliente',
    example: 'juan.perez@email.com',
    required: false,
  })
  @Column({ type: 'text', nullable: true })
  email: string;

  @ApiProperty({ description: 'Fecha de creación del registro' })
  @CreateDateColumn({
    name: 'fechaCreacion',
    type: 'datetime',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fechaCreacion: Date;
}