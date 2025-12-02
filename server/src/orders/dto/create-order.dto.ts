import { ApiProperty } from '@nestjs/swagger';
import {
  IsInt,
  IsOptional,
  IsString,
  IsDateString,
  IsBoolean,
  Min,
  IsNumber,
  IsNotEmpty,
} from 'class-validator';

export class CreateOrderDto {
  @ApiProperty({
    description: 'ID del Cliente al que pertenece la orden',
    required: false,
    example: 1,
  })
  @IsNotEmpty({ message: 'El id_client es obligatorio.' })
  @IsInt()
  id_client: number;

  @ApiProperty({ required: false, example: 5 })
  @IsOptional()
  @IsInt()
  @Min(0)
  extension_tiempo: number;

  @ApiProperty({ required: false, example: 'Reparación de pantalla' })
  @IsOptional()
  @IsString()
  detalle: string;

  @ApiProperty({ description: 'Fecha de entrega (YYYY-MM-DD)', required: false })
  @IsOptional()
  @IsDateString()
  entrega: string;

  @ApiProperty({ default: false, required: false })
  @IsOptional()
  @IsBoolean()
  estado: boolean;

  @ApiProperty({ default: false, required: false })
  @IsOptional()
  @IsBoolean()
  cobrado: boolean;

  @ApiProperty({
    description: 'Total (en centavos o unidad mínima)',
    required: false,
    example: 5000,
  })
  @IsOptional()
  @IsNumber()
  @Min(0)
  total: number;
}