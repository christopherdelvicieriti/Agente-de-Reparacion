import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';
import { UserModule } from './user/user.module';
import { OrdersModule } from './orders/orders.module';
import { ImagesModule } from './images/images.module';
import { ClientsModule } from './clients/clients.module';
import { MachinesModule } from './machines/machines.module';
import { AuthModule } from './auth/auth.module';
import { ScheduleModule } from '@nestjs/schedule';
import { SpacesModule } from './spaces/spaces.module';

@Module({
  imports: [
    ScheduleModule.forRoot(),
    ServeStaticModule.forRoot({
      rootPath: join(__dirname, 'public'),
    }),
    TypeOrmModule.forRoot({
      type: 'sqlite',
      database: 'database.sqlite',
      entities: [
         __dirname + '/**/*.entity{.ts,.js}',
      ],
      synchronize: true,
    }),
    UserModule,
    OrdersModule,
    ImagesModule,
    ClientsModule,
    MachinesModule,
    AuthModule,
    SpacesModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
