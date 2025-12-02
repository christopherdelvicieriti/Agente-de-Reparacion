import { ApiProperty } from '@nestjs/swagger';
import {
  IsString,
  IsNotEmpty,
  MinLength,
  IsOptional,
  IsInt,
  Min,
  IsNumber,
} from 'class-validator';

export class CreateMachineDto {
  @ApiProperty({
    description: 'ID de la Orden a la que se asocia (Obligatorio)',
    example: 1,
  })
  @IsInt({ message: 'El id_order debe ser un número entero.' })
  @IsNotEmpty({ message: 'El id_order es obligatorio.' })
  id_order: number;

  @ApiProperty({
    description: 'ID del Espacio donde se ubica (Opcional)',
    required: false,
    example: 1,
  })
  @IsOptional()
  @IsInt()
  id_spaces: number;

  @ApiProperty({ description: 'Modelo de la máquina', example: 'Singer 2250' })
  @IsString()
  @IsNotEmpty()
  @MinLength(3)
  modelo: string;

  @ApiProperty({ required: false, example: 'Máquina de coser doméstica' })
  @IsOptional()
  @IsString()
  descripcion: string;

  @ApiProperty({ required: false, example: 'Pedal, agujas extra' })
  @IsOptional()
  @IsString()
  accesorios: string;

  @ApiProperty({ description: 'URL/Path de la imagen anverso', required: false })
  @IsOptional()
  @IsString()
  img_anverso: string;

  @ApiProperty({ description: 'URL/Path de la imagen reverso', required: false })
  @IsOptional()
  @IsString()
  img_reverso: string;

  @ApiProperty({ description: 'URL/Path de la imagen accesorios', required: false })
  @IsOptional()
  @IsString()
  img_accesorios: string;

  @ApiProperty({
    description: 'Costo del arreglo (en centavos)',
    required: false,
    example: 2500,
  })
  @IsOptional()
  @IsNumber()
  @Min(0)
  costo_arreglo: number;
}