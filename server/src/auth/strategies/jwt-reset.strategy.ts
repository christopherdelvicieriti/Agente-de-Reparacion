import { resetSecret } from '@/shared/config';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
interface ResetPayload {
  id: number;
  scope: string;
}

@Injectable()
export class JwtResetStrategy extends PassportStrategy(Strategy, 'jwt-reset') {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: resetSecret as string,
    });
  }

  async validate(payload: ResetPayload) {
    if (payload.scope !== 'reset_password' || payload.id !== 1) {
      throw new UnauthorizedException('Token no válido para reseteo');
    }
    return { id: payload.id };
  }
}