package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenService;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;

/**
 * @author Matt Stine
 */
final class DeliveryService {
	private final EventLog eventLog;
	private final DeliveryOrderRepository deliveryOrderRepository;
	private final OrderingService orderingService;
	private final KitchenService kitchenService;

	DeliveryService(EventLog eventLog, DeliveryOrderRepository deliveryOrderRepository, OrderingService orderingService, KitchenService kitchenService) {
		this.eventLog = eventLog;
		this.deliveryOrderRepository = deliveryOrderRepository;
		this.orderingService = orderingService;
		this.kitchenService = kitchenService;
	}

	DeliveryOrder findDeliveryOrderByKitchenOrderRef(KitchenOrderRef kitchenOrderRef) {
		return null;
	}
}
