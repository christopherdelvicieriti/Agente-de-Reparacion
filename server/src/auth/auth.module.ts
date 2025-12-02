import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { secret, user_jwt_expires_in } from '../shared/config';
import { User } from '../user/user.entity';
import { AuthService } from './auth.service';
import { AuthController } from './auth.controller';
import { SetupTokenService } from './setup-token.service';
import { JwtStrategy } from './strategies/jwt.strategy';
import { JwtResetStrategy } from './strategies/jwt-reset.strategy';
import { JwtResetGuard } from './guards/jwt-reset.guard';
import { JwtSetupStrategy } from './strategies/jwt.setup.strategy';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { JwtSetupGuard } from './guards/jwt-setup.guard';

@Module({
  imports: [
    PassportModule.register({ defaultStrategy: 'jwt' }),
    JwtModule.register({
      secret: secret,
      signOptions: {
        expiresIn: user_jwt_expires_in,
      },
    }),
    TypeOrmModule.forFeature([User]),
  ],
  controllers: [
    AuthController,
  ],
  providers: [
    AuthService,
    SetupTokenService,
    JwtStrategy,
    JwtResetStrategy,
    JwtSetupStrategy,
    JwtAuthGuard,
    JwtResetGuard,
    JwtSetupGuard,
  ],
  exports: [
    JwtAuthGuard,
    JwtResetGuard,
    JwtSetupGuard,
    PassportModule,
  ],
})
export class AuthModule {}