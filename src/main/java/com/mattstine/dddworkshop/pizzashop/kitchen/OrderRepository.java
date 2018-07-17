package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Repository;

interface OrderRepository extends Repository<KitchenOrderRef, Order, Order.OrderState, OrderEvent, OrderAddedEvent> {
}
