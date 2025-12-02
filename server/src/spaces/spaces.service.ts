import {
  Injectable,
  NotFoundException,
  ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Space } from './spaces.entity';
import { CreateSpaceDto } from './dto/create-space.dto';
import { UpdateSpaceDto } from './dto/update-space.dto';

@Injectable()
export class SpacesService {
  constructor(
    @InjectRepository(Space)
    private readonly spaceRepository: Repository<Space>,
  ) {}

  async create(createSpaceDto: CreateSpaceDto): Promise<Space> {
    try {
      const newSpace = this.spaceRepository.create(createSpaceDto);
      return await this.spaceRepository.save(newSpace);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async findAll(): Promise<Space[]> {
    return this.spaceRepository.find({
      order: {
        alias: 'ASC',
      },
    });
  }

  async findOne(id: number): Promise<Space> {
    const space = await this.spaceRepository.findOne({ where: { id } });
    if (!space) {
      throw new NotFoundException(`Espacio con ID #${id} no encontrado.`);
    }
    return space;
  }

  async update(id: number, updateSpaceDto: UpdateSpaceDto): Promise<Space> {
    const space = await this.spaceRepository.preload({
      id: id,
      ...updateSpaceDto,
    });

    if (!space) {
      throw new NotFoundException(`Espacio con ID #${id} no encontrado.`);
    }

    try {
      return await this.spaceRepository.save(space);
    } catch (error) {
      this.handleDbError(error);
    }
  }

  async remove(id: number): Promise<void> {
    const result = await this.spaceRepository.delete(id);
    if (result.affected === 0) {
      throw new NotFoundException(`Espacio con ID #${id} no encontrado.`);
    }
  }

  private handleDbError(error: any): never {
    if (error.code === 'SQLITE_CONSTRAINT' && error.message.includes('UNIQUE')) {
      let field = error.message.includes('color') ? 'color' : 'alias';
      throw new ConflictException(`El valor para el campo '${field}' ya existe.`);
    }
    throw error;
  }
}