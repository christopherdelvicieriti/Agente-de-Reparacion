import {
  Entity,
  Column,
  PrimaryColumn,
  CreateDateColumn,
  Check,
} from 'typeorm';
import { ApiProperty } from '@nestjs/swagger';
import { Exclude } from 'class-transformer';

@Entity({ name: 'User' })
@Check('solo_una_fila', 'id = 1')
export class User {

  @ApiProperty({ description: 'El ID único del usuario (siempre 1)', example: 1 })
  @PrimaryColumn({ type: 'integer', default: 1 })
  id: number;

  @ApiProperty({ description: 'El nombre de usuario', example: 'admin' })
  @Column({ type: 'text', unique: true, nullable: false })
  usuario: string;

  @Exclude()
  @Column({ type: 'text', nullable: false })
  contraseña: string;

  @Exclude()
  @Column({ name: 'secret_key', type: 'text', nullable: false })
  secret_key: string;

  @ApiProperty({ description: 'La fecha de creación del usuario' })
  @CreateDateColumn({
    name: 'fechaCreacion',
    type: 'datetime',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fechaCreacion: Date;
}