import { ApiProperty } from '@nestjs/swagger';
import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
} from 'typeorm';

@Entity({ name: 'Spaces' })
export class Space {
  @ApiProperty({ description: 'ID único del espacio', example: 1 })
  @PrimaryGeneratedColumn()
  id: number;

  @ApiProperty({
    description: 'Alias o nombre corto único del espacio',
    example: 'Taller',
  })
  @Column({ type: 'text', unique: true, nullable: false })
  alias: string;

  @ApiProperty({
    description: 'Descripción detallada del espacio',
    required: false,
    example: 'Taller principal de reparaciones',
  })
  @Column({ type: 'text', nullable: true })
  descripcion: string;

  @ApiProperty({ description: 'URL o path de la imagen', required: false })
  @Column({ name: 'image', type: 'text', nullable: true })
  image: string;

  @ApiProperty({
    description: 'Color hexadecimal único para identificar el espacio',
    required: false,
    example: '#FF5733',
  })
  @Column({ type: 'text', unique: true, nullable: true })
  color: string;

  @ApiProperty({ description: 'Fecha de creación del registro' })
  @CreateDateColumn({
    name: 'fechaCreacion',
    type: 'datetime',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fechaCreacion: Date;
}