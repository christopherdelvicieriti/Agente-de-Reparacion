import {
  Injectable,
  NotFoundException,
  UnauthorizedException,
  ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from './user.entity';
import * as bcrypt from 'bcrypt';

import { UserUpdateDto } from './dto/user-update.dto';
import { UserPasswordUpdateDto } from './dto/user-password-update.dto';

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
  ) {}

  async getProfile(id: number): Promise<Omit<User, 'contraseña' | 'secret_key'>> {
    const user = await this.userRepository.findOne({ where: { id } });
    if (!user) {
      throw new NotFoundException('Usuario no encontrado');
    }
    const { contraseña, secret_key, ...result } = user;
    return result;
  }

  async updateUser(id: number, userUpdateDto: UserUpdateDto): Promise<Omit<User, 'contraseña' | 'secret_key'>> {
    const user = await this.userRepository.findOne({ where: { id } });
    if (!user) {
      throw new NotFoundException('Usuario no encontrado');
    }
    user.usuario = userUpdateDto.usuario;
    try {
      await this.userRepository.save(user);
    } catch (error) {
      if (error.code === 'SQLITE_CONSTRAINT' && error.message.includes('UNIQUE')) {
        throw new ConflictException(`El nombre de usuario '${userUpdateDto.usuario}' ya existe.`);
      }
      throw error;
    }
    const { contraseña, secret_key, ...result } = user;
    return result;
  }

  async updatePassword(id: number, passDto: UserPasswordUpdateDto): Promise<{ message: string }> {
    const user = await this.userRepository.findOne({ where: { id } });

    if (!user) {
      throw new NotFoundException('Usuario no encontrado');
    }

    const isMatch = await bcrypt.compare(passDto.oldPassword, user.contraseña);
    if (!isMatch) {
      throw new UnauthorizedException('La contraseña antigua es incorrecta.');
    }

    const salt = await bcrypt.genSalt();
    user.contraseña = await bcrypt.hash(passDto.newPassword, salt);
    await this.userRepository.save(user);

    return { message: 'Contraseña actualizada correctamente.' };
  }
}