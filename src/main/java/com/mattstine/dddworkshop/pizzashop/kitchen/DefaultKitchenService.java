package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import lombok.Value;

import java.util.Set;

@Value
final class DefaultKitchenService implements KitchenService {
	EventLog eventLog;
	KitchenOrderRepository kitchenOrderRepository;
	PizzaRepository pizzaRepository;
	OrderingService orderingService;

	DefaultKitchenService(EventLog eventLog, KitchenOrderRepository kitchenOrderRepository, PizzaRepository pizzaRepository, OrderingService orderingService) {
		this.kitchenOrderRepository = kitchenOrderRepository;
		this.eventLog = eventLog;
		this.pizzaRepository = pizzaRepository;
		this.orderingService = orderingService;
	}

	@Override
	public void startOrderPrep(KitchenOrderRef kitchenOrderRef) {
	}

	@Override
	public void finishPizzaPrep(PizzaRef ref) {
	}

	@Override
	public void removePizzaFromOven(PizzaRef ref) {
	}

	@Override
	public KitchenOrder findKitchenOrderByRef(KitchenOrderRef kitchenOrderRef) {
		return null;
	}

	@Override
	public KitchenOrder findKitchenOrderByOnlineOrderRef(OnlineOrderRef onlineOrderRef) {
		return null;
	}

	@Override
	public Pizza findPizzaByRef(PizzaRef ref) {
		return null;
	}

	@Override
	public Set<Pizza> findPizzasByKitchenOrderRef(KitchenOrderRef kitchenOrderRef) {
		return null;
	}

}
