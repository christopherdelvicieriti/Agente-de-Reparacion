import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Order } from './orders.entity';
import { Client } from '../clients/clients.entity';
import { CreateOrderDto } from './dto/create-order.dto';
import { UpdateOrderDto } from './dto/update-order.dto';

@Injectable()
export class OrdersService {
  constructor(
    @InjectRepository(Order)
    private readonly orderRepository: Repository<Order>,
    @InjectRepository(Client)
    private readonly clientRepository: Repository<Client>,
  ) {}

  async create(createOrderDto: CreateOrderDto): Promise<Order> {

    const { id_client, ...orderData } = createOrderDto;

    const client = await this.clientRepository.findOne({
      where: { id: id_client },
    });
    if (!client) {
      throw new NotFoundException(`Cliente con ID #${id_client} no encontrado.`);
    }

    const newOrder = this.orderRepository.create({
      ...orderData,
      client: client,
    });

    return this.orderRepository.save(newOrder);
  }

  async findAll(): Promise<Order[]> {
    return this.orderRepository.find({
      order: {
        fechaCreacion: 'DESC',
      },
    });
  }

  async findOne(id: number): Promise<Order> {
    const order = await this.orderRepository.findOne({ where: { id } });
    if (!order) {
      throw new NotFoundException(`Orden con ID #${id} no encontrada.`);
    }
    return order;
  }

  async update(id: number, updateOrderDto: UpdateOrderDto): Promise<Order> {
    const { id_client, ...orderData } = updateOrderDto;

    const order = await this.orderRepository.preload({
      id: id,
      ...orderData,
    });

    if (!order) {
      throw new NotFoundException(`Orden con ID #${id} no encontrada.`);
    }

    if (id_client) {
      const client = await this.clientRepository.findOne({
        where: { id: id_client },
      });
      if (!client) {
        throw new NotFoundException(`Cliente con ID #${id_client} no encontrado.`);
      }
      order.client = client;
    }

    return this.orderRepository.save(order);
  }

  async remove(id: number): Promise<void> {
    const result = await this.orderRepository.delete(id);
    if (result.affected === 0) {
      throw new NotFoundException(`Orden con ID #${id} no encontrada.`);
    }
  }
}