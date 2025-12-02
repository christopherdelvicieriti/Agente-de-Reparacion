import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { secret } from '../../shared/config';
export interface JwtPayload {
  usuario: string;
  id: number;
}

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy, 'jwt') {
  constructor() {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      ignoreExpiration: false,
      secretOrKey: secret as string,
    });
  }

  async validate(payload: JwtPayload) {
    if (payload.id !== 1) {
       throw new UnauthorizedException('Token inválido');
    }

    return { id: payload.id, usuario: payload.usuario };
  }
}