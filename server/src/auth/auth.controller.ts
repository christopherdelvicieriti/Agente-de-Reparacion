import {
  Controller,
  Post,
  Body,
  UseGuards,
  HttpCode,
  HttpStatus,
  Get,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { AuthCredentialsDto } from './dto/auth-credentials.dto';
import {
  ApiTags,
  ApiOperation,
  ApiResponse,
  ApiBearerAuth,
  ApiBody,
} from '@nestjs/swagger';
import { AuthResetPasswordDto } from './dto/auth-reset-password.dto';
import { User } from '../user/user.entity';
import { GetUser } from './auth.decorator';
import { JwtResetGuard } from './guards/jwt-reset.guard';
import { JwtSetupGuard } from './guards/jwt-setup.guard';

@ApiTags('Auth')
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Get('status')
  @ApiOperation({
    summary: 'Verificar estado del sistema',
    description: 'Devuelve true si el admin ya existe, false si no existe'
  })
  @ApiResponse({
    status: 200,
    description: 'Estado del sistema.',
    schema: {
      example: { isConfigured: true }
    }
  })
  async checkSystemStatus() {
    return this.authService.isSystemConfigured();
  }

  @Post('setup')
  @ApiBearerAuth('setup-token')
  @UseGuards(JwtSetupGuard)
  @ApiOperation({ summary: 'Crear usuario' })
  @ApiResponse({ status: 201, description: 'Usuario configurado, se entrega "Clave Secreta" para la recuperación de la cuenta.' })
  @ApiResponse({ status: 401, description: 'Token de setup inválido o faltante.' })
  async setupUser(@Body() authDto: AuthCredentialsDto): Promise<{ message: string, secret_key: string }> {
    return this.authService.createOrUpdateUser(authDto);
  }

  @Post('login')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Iniciar sesión' })
  @ApiResponse({ status: 200, description: 'Login exitoso, se entrega JWT.' })
  @ApiResponse({ status: 401, description: 'Credenciales incorrectas.' })
  async login(@Body() authDto: AuthCredentialsDto): Promise<{ access_token: string }> {
    return this.authService.login(authDto);
  }

  @Post('recover')
  @HttpCode(HttpStatus.OK)
  @ApiOperation({ summary: 'Obtener JWT de reseteo mediante "Clave Secreta" para restablecer contraseña' })
  @ApiBody({
    schema: {
      type: 'object',
      properties: { secret_key: { type: 'string' } },
    },
  })
  @ApiResponse({ status: 200, description: '"Clave secreta" válida, se entrega token de reseteo.' })
  @ApiResponse({ status: 401, description: 'Secret key inválido.' })
  async recoverAccount(@Body('secret_key') secretKey: string): Promise<{ reset_token: string }> {
    return this.authService.createResetToken(secretKey);
  }

  @Post('reset-password')
  @HttpCode(HttpStatus.OK)
  @ApiBearerAuth('reset-token')
  @UseGuards(JwtResetGuard)
  @ApiOperation({ summary: 'Restablecer contraseña mediante JWT de reseteo' })
  @ApiResponse({ status: 200, description: 'Contraseña actualizada.' })
  @ApiResponse({ status: 401, description: 'Token de reseteo inválido o expirado.' })
  async resetPassword(
    @GetUser() user: User,
    @Body() resetDto: AuthResetPasswordDto,
  ): Promise<{ message: string }> {
    return this.authService.resetPassword(user.id, resetDto);
  }
}