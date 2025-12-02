import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { Machine } from './machines.entity';
import { Order } from '../orders/orders.entity';
import { Space } from '../spaces/spaces.entity';
import { CreateMachineDto } from './dto/create-machine.dto';
import { UpdateMachineDto } from './dto/update-machine.dto';

@Injectable()
export class MachinesService {
  constructor(
    @InjectRepository(Machine)
    private readonly machineRepository: Repository<Machine>,
    @InjectRepository(Order)
    private readonly orderRepository: Repository<Order>,
    @InjectRepository(Space)
    private readonly spaceRepository: Repository<Space>,
    private readonly dataSource: DataSource,
  ) {}

  async create(createDto: CreateMachineDto): Promise<Machine> {
    const { id_order, id_spaces, costo_arreglo, ...machineData } = createDto;

    const order = await this.orderRepository.findOne({ where: { id: id_order } });
    if (!order) {
      throw new NotFoundException(`Orden con ID #${id_order} no encontrada.`);
    }

    let space: Space | null = null;
    if (id_spaces) {
      space = await this.spaceRepository.findOne({ where: { id: id_spaces } });
      if (!space) {
        throw new NotFoundException(`Espacio con ID #${id_spaces} no encontrado.`);
      }
    }

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const newMachine = queryRunner.manager.create(Machine, {
        ...machineData,
        costo_arreglo: costo_arreglo || 0,
        order: order,
        space: space,
      });
      const savedMachine = await queryRunner.manager.save(newMachine);

      if (costo_arreglo && costo_arreglo > 0) {
        order.total = (order.total || 0) + costo_arreglo;
        await queryRunner.manager.save(order);
      }

      await queryRunner.commitTransaction();
      return savedMachine;
    } catch (error) {
      await queryRunner.rollbackTransaction();
      throw new BadRequestException('Error al crear la máquina: ' + error.message);
    } finally {
      await queryRunner.release();
    }
  }

async update(id: number, updateDto: UpdateMachineDto): Promise<Machine> {
    const { id_order, id_spaces, costo_arreglo, ...machineData } = updateDto;

    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const machine = await queryRunner.manager.findOne(Machine, {
        where: { id },
        relations: ['order', 'space'],
      });
      if (!machine) {
        throw new NotFoundException(`Máquina con ID #${id} no encontrada.`);
      }

      const oldCost = machine.costo_arreglo || 0;
      const oldOrder = machine.order;
      let targetOrder = oldOrder;

      if (id_order && id_order !== oldOrder.id) {
        const newOrder = await queryRunner.manager.findOne(Order, { where: { id: id_order } });
        if (!newOrder) throw new NotFoundException(`Nueva Orden con ID #${id_order} no encontrada.`);
        targetOrder = newOrder;

        let tempOldTotal = (oldOrder.total || 0) - oldCost;
        if (tempOldTotal < 0) tempOldTotal = 0;
        oldOrder.total = parseFloat(tempOldTotal.toFixed(2));

        await queryRunner.manager.save(oldOrder);
      }

      if (id_spaces && id_spaces !== machine.space?.id) {
        const newSpace = await queryRunner.manager.findOne(Space, { where: { id: id_spaces } });
        if (!newSpace) throw new NotFoundException(`Nuevo Espacio con ID #${id_spaces} no encontrado.`);
        machine.space = newSpace;
      } else if (updateDto.hasOwnProperty('id_spaces') && updateDto.id_spaces === null) {
        machine.space = null;
      }

      const newCost = (costo_arreglo !== undefined) ? (costo_arreglo || 0) : oldCost;

      if (targetOrder.id === oldOrder.id) {

        if (newCost !== oldCost) {

          let tempTargetTotal = (targetOrder.total || 0) - oldCost + newCost;
          if (tempTargetTotal < 0) tempTargetTotal = 0;
          targetOrder.total = parseFloat(tempTargetTotal.toFixed(2));

          await queryRunner.manager.save(targetOrder);
        }
      } else {

        const tempTargetTotal = (targetOrder.total || 0) + newCost;
        targetOrder.total = parseFloat(tempTargetTotal.toFixed(2));

        await queryRunner.manager.save(targetOrder);
      }

      queryRunner.manager.merge(Machine, machine, {
          ...machineData,
          costo_arreglo: newCost,
          order: targetOrder,
      });
      const updatedMachine = await queryRunner.manager.save(machine);

      await queryRunner.commitTransaction();
      return updatedMachine;

    } catch (error) {
      await queryRunner.rollbackTransaction();
      throw new BadRequestException('Error al actualizar: ' + error.message);
    } finally {
      await queryRunner.release();
    }
  }

  async remove(id: number): Promise<void> {
    const queryRunner = this.dataSource.createQueryRunner();
    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      const machine = await queryRunner.manager.findOne(Machine, {
        where: { id },
        relations: ['order'],
      });
      if (!machine) {
        throw new NotFoundException(`Máquina con ID #${id} no encontrada.`);
      }

      if (machine.costo_arreglo && machine.costo_arreglo > 0 && machine.order) {
        const order = machine.order;
        order.total = (order.total || 0) - machine.costo_arreglo;
        if (order.total < 0) order.total = 0;
        await queryRunner.manager.save(order);
      }

      await queryRunner.manager.remove(machine);
      await queryRunner.commitTransaction();
    } catch (error) {
      await queryRunner.rollbackTransaction();
      throw new BadRequestException('Error al eliminar: ' + error.message);
    } finally {
      await queryRunner.release();
    }
  }


  async findAll(): Promise<Machine[]> {
    return this.machineRepository.find();
  }

  async findOne(id: number): Promise<Machine> {
    const machine = await this.machineRepository.findOne({ where: { id } });
    if (!machine) {
      throw new NotFoundException(`Máquina con ID #${id} no encontrada.`);
    }
    return machine;
  }

  async findByOrder(orderId: number): Promise<Machine[]> {
    return this.machineRepository.find({
      where: {
        order: { id: orderId },
      },
    });
  }

  async findBySpace(spaceId: number): Promise<Machine[]> {
    return this.machineRepository.find({
      where: {
        space: { id: spaceId },
      },
    });
  }
}