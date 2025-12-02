import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNotEmpty, MinLength, IsOptional, IsEmail } from 'class-validator';

export class CreateClientDto {
  @ApiProperty({
    description: 'Nombre completo del cliente',
    example: 'Juan Pérez',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(3)
  nombre: string;

  @ApiProperty({ required: false, example: 'Av. Siempre Viva 123' })
  @IsOptional()
  @IsString()
  dirección: string;

  @ApiProperty({ required: false, example: '1234567890' })
  @IsOptional()
  @IsString()
  cedula: string;

  @ApiProperty({ required: false, example: '0991234567' })
  @IsOptional()
  @IsString()
  telf1: string;

  @ApiProperty({ required: false, example: '022345678' })
  @IsOptional()
  @IsString()
  telf2: string;

  @ApiProperty({ required: false, example: 'juan.perez@email.com' })
  @IsOptional()
  @IsEmail()
  email: string;
}