import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Machine } from './machines.entity';
import { Order } from '@/orders/orders.entity';
import { Space } from '@/spaces/spaces.entity';
import { MachinesController } from './machines.controller';
import { AuthModule } from '@/auth/auth.module';
import { MachinesService } from './machines.service';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      Machine,
      Order,
      Space,
    ]),
    AuthModule,
  ],
  controllers: [MachinesController],
  providers: [MachinesService],
})

export class MachinesModule {}