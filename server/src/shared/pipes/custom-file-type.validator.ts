import { FileValidator } from '@nestjs/common';

export class CustomFileTypeValidator extends FileValidator<{ allowedTypes: RegExp }> {
  constructor(options: { allowedTypes: RegExp }) {
    super(options);
  }

  isValid(file?: Express.Multer.File): boolean {
    if (!file) {
      return false;
    }
    return this.validationOptions.allowedTypes.test(file.mimetype);
  }

  buildErrorMessage(file: any): string {
    return `El tipo de archivo ${file.mimetype} no es válido. Tipos permitidos: ${this.validationOptions.allowedTypes}`;
  }
}