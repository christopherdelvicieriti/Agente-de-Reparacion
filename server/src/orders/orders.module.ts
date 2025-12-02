import { Module } from '@nestjs/common';
import { OrdersController } from './orders.controller';
import { OrdersService } from './orders.service';
import { AuthModule } from '@/auth/auth.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Order } from './orders.entity';
import { Client } from '@/clients/clients.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      Order,
      Client,
    ]),
    AuthModule,
  ],
  controllers: [OrdersController],
  providers: [OrdersService],
})
export class OrdersModule {}