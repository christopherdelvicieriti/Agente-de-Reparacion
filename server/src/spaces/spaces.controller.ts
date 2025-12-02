import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  Delete,
  UseGuards,
  ParseIntPipe,
  HttpCode,
  HttpStatus,
} from '@nestjs/common';
import { SpacesService } from './spaces.service';
import { CreateSpaceDto } from './dto/create-space.dto';
import { UpdateSpaceDto } from './dto/update-space.dto';

import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import {
  ApiBearerAuth,
  ApiOperation,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { Space } from './spaces.entity';

@ApiTags('Spaces')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('spaces')
export class SpacesController {
  constructor(private readonly spacesService: SpacesService) {}

  @Post()
  @ApiOperation({ summary: 'Crear un nuevo espacio' })
  @ApiResponse({ status: 201, description: 'Espacio creado.', type: Space })
  @ApiResponse({ status: 409, description: 'Conflicto (alias o color ya existen).' })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  create(@Body() createSpaceDto: CreateSpaceDto) {
    return this.spacesService.create(createSpaceDto);
  }

  @Get()
  @ApiOperation({ summary: 'Obtener una lista de todos los espacios' })
  @ApiResponse({ status: 200, description: 'Lista de espacios.', type: [Space] })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  findAll() {
    return this.spacesService.findAll();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Obtener un espacio por su ID' })
  @ApiResponse({ status: 200, description: 'Espacio encontrado.', type: Space })
  @ApiResponse({ status: 404, description: 'Espacio no encontrado.' })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.spacesService.findOne(id);
  }

  @Patch(':id')
  @ApiOperation({ summary: 'Actualizar un espacio por su ID' })
  @ApiResponse({ status: 200, description: 'Espacio actualizado.', type: Space })
  @ApiResponse({ status: 404, description: 'Espacio no encontrado.' })
  @ApiResponse({ status: 409, description: 'Conflicto (alias o color ya existen).' })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateSpaceDto: UpdateSpaceDto,
  ) {
    return this.spacesService.update(id, updateSpaceDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: 'Eliminar un espacio por su ID' })
  @ApiResponse({ status: 204, description: 'Espacio eliminado.' })
  @ApiResponse({ status: 404, description: 'Espacio no encontrado.' })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  remove(@Param('id', ParseIntPipe) id: number) {
    return this.spacesService.remove(id);
  }
}