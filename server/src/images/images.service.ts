import { Injectable } from '@nestjs/common';

@Injectable()
export class ImagesService {
  getFilePath(filename: string): string {
    return `images/${filename}`;
  }
}