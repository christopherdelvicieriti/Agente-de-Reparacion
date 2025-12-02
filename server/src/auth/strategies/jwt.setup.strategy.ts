import { setupSecret } from '@/shared/config';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
interface SetupPayload {
  scope: string;
}

@Injectable()
export class JwtSetupStrategy extends PassportStrategy(Strategy, 'jwt-setup') {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: setupSecret as string,
    });
  }

  async validate(payload: SetupPayload) {
    if (payload.scope !== 'setup_app') {
      throw new UnauthorizedException('Token no válido para setup');
    }
    return { authorized: true };
  }
}