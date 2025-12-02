import { IsString, MinLength, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AuthResetPasswordDto {
  @ApiProperty({
    description: 'La nueva contraseña (mínimo 8 caracteres)',
    example: 'newStrongPassword456',
  })
  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  newPassword: string;
}