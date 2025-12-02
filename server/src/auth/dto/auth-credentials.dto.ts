import { IsString, MinLength, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AuthCredentialsDto {
  @ApiProperty({
    description: 'Nombre de usuario',
    example: 'admin',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(3)
  usuario: string;

  @ApiProperty({
    description: 'Contraseña (mínimo 8 caracteres)',
    example: 'supersecret123',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  contraseña: string;
}