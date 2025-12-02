import { IsString, MinLength, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class UserPasswordUpdateDto {
  @ApiProperty({
    description: 'La contraseña actual del usuario',
    example: 'supersecret123',
  })
  @IsString()
  @IsNotEmpty()
  oldPassword: string;

  @ApiProperty({
    description: 'La nueva contraseña (mínimo 1 caracteres)',
    example: 'newStrongPassword456',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  newPassword: string;
}