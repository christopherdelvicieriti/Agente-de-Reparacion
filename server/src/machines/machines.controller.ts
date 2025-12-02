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
import { MachinesService } from './machines.service';
import { CreateMachineDto } from './dto/create-machine.dto';
import { UpdateMachineDto } from './dto/update-machine.dto';

import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import {
  ApiBearerAuth,
  ApiOperation,
  ApiResponse,
  ApiTags,
} from '@nestjs/swagger';
import { Machine } from './machines.entity';

@ApiTags('Machines')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('machines')
export class MachinesController {
  constructor(private readonly machinesService: MachinesService) {}

  @Post()
  @ApiOperation({ summary: 'Crear una nueva máquina' })
  @ApiResponse({ status: 201, description: 'Máquina creada.', type: Machine })
  @ApiResponse({ status: 404, description: 'Orden o Espacio no encontrado.' })
  @ApiResponse({ status: 401, description: 'No autorizado.' })
  create(@Body() createMachineDto: CreateMachineDto) {
    return this.machinesService.create(createMachineDto);
  }

  @Get()
  @ApiOperation({ summary: 'Obtener una lista de todas las máquinas' })
  @ApiResponse({ status: 200, description: 'Lista de máquinas.', type: [Machine] })
  findAll() {
    return this.machinesService.findAll();
  }

  @Get('by-order/:id')
  @ApiOperation({ summary: 'Obtener máquinas por ID de Orden' })
  @ApiResponse({ status: 200, type: [Machine] })
  findByOrder(@Param('id', ParseIntPipe) id: number) {
    return this.machinesService.findByOrder(id);
  }

  @Get('by-space/:id')
  @ApiOperation({ summary: 'Obtener máquinas por ID de Espacio' })
  @ApiResponse({ status: 200, type: [Machine] })
  findBySpace(@Param('id', ParseIntPipe) id: number) {
    return this.machinesService.findBySpace(id);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Obtener una máquina por su ID' })
  @ApiResponse({ status: 200, description: 'Máquina encontrada.', type: Machine })
  @ApiResponse({ status: 404, description: 'Máquina no encontrada.' })
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.machinesService.findOne(id);
  }

  @Patch(':id')
  @ApiOperation({ summary: 'Actualizar una máquina por su ID' })
  @ApiResponse({ status: 200, description: 'Máquina actualizada.', type: Machine })
  @ApiResponse({ status: 404, description: 'Máquina/Orden/Espacio no encontrado.' })
  update(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateMachineDto: UpdateMachineDto,
  ) {
    return this.machinesService.update(id, updateMachineDto);
  }

  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiOperation({ summary: 'Eliminar una máquina por su ID' })
  @ApiResponse({ status: 204, description: 'Máquina eliminada.' })
  @ApiResponse({ status: 404, description: 'Máquina no encontrada.' })
  remove(@Param('id', ParseIntPipe) id: number) {
    return this.machinesService.remove(id);
  }
}