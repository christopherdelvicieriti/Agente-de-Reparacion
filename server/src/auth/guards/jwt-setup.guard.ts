import { Injectable } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtSetupGuard extends AuthGuard('jwt-setup') {}