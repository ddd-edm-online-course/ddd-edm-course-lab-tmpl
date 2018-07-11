package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.infrastructure.Amount;
import com.mattstine.dddworkshop.pizzashop.infrastructure.EventLog;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matt Stine
 */
public class Order {
	private final OrderType type;
	private final EventLog eventLog;
	private final OrderRef id;
	private OrderState state;
	private List<Pizza> pizzas;
	private PaymentRef paymentRef;

	private Order(OrderType type, EventLog eventLog, OrderRef ref) {
		this.type = type;
		this.eventLog = eventLog;
		this.id = ref;
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

	public List<Pizza> getPizzas() {
		return pizzas;
	}

	public Amount getPrice() {
		return this.pizzas.stream()
				.map(Pizza::getPrice)
				.reduce(Amount.of(0,0), Amount::plus);
	}

	public PaymentRef getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(PaymentRef paymentRef) {
		this.paymentRef = paymentRef;
	}

	static class OrderBuilder {
		private OrderType type;
		private EventLog eventLog;
		private OrderRef ref;

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

			if (this.ref == null) {
				throw new IllegalStateException("Cannot build Order without valid Id");
			}

			return new Order(this.type, this.eventLog, this.ref);
		}

		public OrderBuilder withId(OrderRef ref) {
			this.ref = ref;
			return this;
		}
	}
}
