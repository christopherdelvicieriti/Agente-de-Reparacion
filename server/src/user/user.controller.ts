import {
  Controller,
  Body,
  Get,
  UseGuards,
  Put,
} from '@nestjs/common';
import { UserService } from './user.service';
import { User } from './user.entity';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { UserUpdateDto } from './dto/user-update.dto';
import { UserPasswordUpdateDto } from './dto/user-password-update.dto';
import { JwtAuthGuard } from '@/auth/guards/jwt-auth.guard';
import { GetUser } from './user.decorator';

@ApiTags('User')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('user')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Get('profile')
  @ApiOperation({ summary: 'Obtener el perfil del usuario' })
  @ApiResponse({ status: 200, description: 'Perfil del usuario.', type: User })
  async getProfile(@GetUser() user: User) {
    return this.userService.getProfile(user.id);
  }

  @Put('profile')
  @ApiOperation({ summary: 'Actualizar perfil de usuario' })
  async updateProfile(
    @GetUser() user: User,
    @Body() userUpdateDto: UserUpdateDto,
  ) {
    return this.userService.updateUser(user.id, userUpdateDto);
  }

  @Put('password')
  @ApiOperation({ summary: 'Actualizar la contraseña' })
  async updatePassword(
    @GetUser() user: User,
    @Body() passwordDto: UserPasswordUpdateDto,
  ) {
    return this.userService.updatePassword(user.id, passwordDto);
  }
}