import { ApiProperty } from '@nestjs/swagger';
import {
  IsString,
  IsNotEmpty,
  MinLength,
  IsOptional,
  IsHexColor,
} from 'class-validator';

export class CreateSpaceDto {
  @ApiProperty({
    description: 'Alias o nombre corto único del espacio',
    example: 'Taller',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(3)
  alias: string;

  @ApiProperty({
    description: 'Descripción detallada del espacio',
    required: false,
    example: 'Taller principal de reparaciones',
  })
  @IsOptional()
  @IsString()
  descripcion: string;

  @ApiProperty({ description: 'URL/Path de la imagen anverso', required: false })
  @IsOptional()
  @IsString()
  image: string;

  @ApiProperty({
    description: 'Color hexadecimal único (ej. #FF5733)',
    required: false,
    example: '#FF5733',
  })
  @IsOptional()
  @IsHexColor()
  color: string;
}