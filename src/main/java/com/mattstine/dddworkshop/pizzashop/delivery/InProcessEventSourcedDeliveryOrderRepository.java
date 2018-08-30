package com.mattstine.dddworkshop.pizzashop.delivery;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;
import com.mattstine.dddworkshop.pizzashop.kitchen.KitchenOrderRef;

/**
 * @author Matt Stine
 */
final class InProcessEventSourcedDeliveryOrderRepository extends InProcessEventSourcedRepository<DeliveryOrderRef, DeliveryOrder, DeliveryOrder.OrderState, DeliveryOrderEvent, DeliveryOrderAddedEvent> implements DeliveryOrderRepository {

	InProcessEventSourcedDeliveryOrderRepository(EventLog eventLog, Topic topic) {
		super(eventLog,
				DeliveryOrderRef.class,
				DeliveryOrder.class,
				DeliveryOrder.OrderState.class,
				DeliveryOrderAddedEvent.class,
				topic);
	}

	@Override
	public DeliveryOrder findByKitchenOrderRef(KitchenOrderRef kitchenOrderRef) {
		return null;
	}
}
