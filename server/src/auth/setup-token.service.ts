import { Injectable, Logger, OnApplicationBootstrap } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { SchedulerRegistry } from '@nestjs/schedule';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { User } from '../user/user.entity';
import { setup_jwt_expires_in, setupSecret, port } from '../shared/config';

import * as qrcode from 'qrcode-terminal';
import * as os from 'os';

const SETUP_INTERVAL_NAME = 'setup-token-generator';
const TREINTA_MINUTOS_MS = 30 * 60 * 1000;

@Injectable()
export class SetupTokenService implements OnApplicationBootstrap {
  private readonly logger = new Logger(SetupTokenService.name);

  constructor(
    private readonly jwtService: JwtService,
    private readonly schedulerRegistry: SchedulerRegistry,
    @InjectRepository(User)
    private readonly userRepository: Repository<User>,
  ) {}

  async onApplicationBootstrap() {
    const userExists = await this.doesAdminUserExist();

    if (userExists) {
      return;
    }

    this.generateAndLogToken();

    const interval = setInterval(
      () => this.generateAndLogToken(),
      TREINTA_MINUTOS_MS,
    );
    this.schedulerRegistry.addInterval(SETUP_INTERVAL_NAME, interval);
  }

  async generateAndLogToken() {
    const userExists = await this.doesAdminUserExist();

    if (userExists) {
      this.logger.log('Usuario creado. Deteniendo setup.');
      this.schedulerRegistry.deleteInterval(SETUP_INTERVAL_NAME);
      return;
    }

    const payload = { scope: 'setup_app' };
    const token = this.jwtService.sign(payload, {
      secret: setupSecret,
      expiresIn: setup_jwt_expires_in,
    });

    const ip = this.getLocalIpAddress();
    const currentPort = port || 3000;
    const url = `http://${ip}:${currentPort}`;

    console.log('\n');

    console.log('\x1b[36m%s\x1b[0m', '> QR Creación de Usuario ( Expires 30m )');

    const qrOptions = { small: true, errorCorrectionLevel: 'L' as const };
    qrcode.generate(token, qrOptions, (qr) => {
        console.log(qr);
    });

    console.log('\x1b[32m%s\x1b[0m', `> URL local: ${url}`);
    console.log('\n');
  }

  private async doesAdminUserExist(): Promise<boolean> {
    try {
      const user = await this.userRepository.findOne({ where: { id: 1 } });
      return !!user;
    } catch (error) {
      return false;
    }
  }

  private getLocalIpAddress(): string {
    const interfaces = os.networkInterfaces();
    for (const devName in interfaces) {
      const iface = interfaces[devName];
      if (!iface) continue;
      for (let i = 0; i < iface.length; i++) {
        const alias = iface[i];
        if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal) {
          return alias.address;
        }
      }
    }
    return 'localhost';
  }
}