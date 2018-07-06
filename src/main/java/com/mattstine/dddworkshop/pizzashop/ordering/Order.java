package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matt Stine
 */
public class Order {
	private final OrderType type;
	private final EventLog eventLog;
	private OrderState state;
	private List<Pizza> pizzas;

	private Order(OrderType type, EventLog eventLog) {
		this.type = type;
		this.eventLog = eventLog;
		this.pizzas = new ArrayList<>();
	}

	public static OrderBuilder withType(OrderType type) {
		return new OrderBuilder(type);
	}

	public static OrderBuilder withEventLog(EventLog eventLog) {
		return new OrderBuilder(eventLog);
	}

	public boolean isPickupOrder() {
		return this.type == OrderType.PICKUP;
	}

	public boolean isDeliveryOrder() {
		return this.type == OrderType.DELIVERY;
	}

	public boolean isSubmitted() {
		return this.state == OrderState.SUBMITTED;
	}

	public void addPizza(Pizza pizza) {
		this.pizzas.add(pizza);
	}

	public void submit() {
		if (this.pizzas.isEmpty()) {
			throw new IllegalStateException("Cannot submit Order without at least one Pizza");
		}

		this.state = OrderState.SUBMITTED;
		eventLog.publish(new OrderPlacedEvent());
	}

	static class OrderBuilder {
		private OrderType type;
		private EventLog eventLog;

		OrderBuilder(OrderType type) {
			this.type = type;
		}

		OrderBuilder(EventLog eventLog) {
			this.eventLog = eventLog;
		}

		public OrderBuilder withEventLog(EventLog eventLog) {
			this.eventLog = eventLog;
			return this;
		}

		public OrderBuilder withType(OrderType type) {
			this.type = type;
			return this;
		}

		Order build() {
			if (this.type == null) {
				throw new IllegalStateException("Cannot build Order without valid OrderType");
			}

			if (this.eventLog == null) {
				throw new IllegalStateException("Cannot build Order without valid EventLog");
			}

			return new Order(this.type, this.eventLog);
		}
	}
}
