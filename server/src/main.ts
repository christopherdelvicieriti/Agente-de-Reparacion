import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { port } from './shared/config';
import helmet from "helmet";
import { ValidationPipe } from "@nestjs/common";
import { urlencoded } from "express";

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.use(
    helmet({
      crossOriginResourcePolicy: { policy: "cross-origin" },
    }),
  );

  app.enableCors({
    origin: [
      "https://christopherdelvicieriti",
      "https://delvicier.github.io",
      "http://localhost:4000",
      "http://127.0.0.1:4000",
    ],
    methods: "GET,HEAD,PUT,PATCH,POST,DELETE",
    credentials: true,
    allowedHeaders: "Content-Type, Accept, Authorization",
  });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      transformOptions: {
        enableImplicitConversion: true,
      },
    }),
  );

  const config = new DocumentBuilder()
    .setTitle('DHC API')
    .setDescription('API documentation for DHC application')
    .setVersion('1.0')
    .build();

  const document = SwaggerModule.createDocument(app, config);

  SwaggerModule.setup('api', app, document);

  app.use(urlencoded({ extended: true, limit: "5mb" }));

  await app.listen(port ?? 4000);
}
bootstrap();
